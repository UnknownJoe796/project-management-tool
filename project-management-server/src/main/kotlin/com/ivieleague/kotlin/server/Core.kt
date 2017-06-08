package com.ivieleague.kotlin.server

import graphql.Scalars
import graphql.schema.*

typealias AccessRules = List<List<AccessRule>>

class Table(val name: String, val description: String) {
    val properties = HashMap<String, Property<*, *>>()
}

sealed class ServerType<KOTLIN> {
    object TBoolean : ServerType<Boolean>()

    object TByte : ServerType<Byte>()
    object TShort : ServerType<Short>()
    object TInt : ServerType<Int>()
    object TLong : ServerType<Long>()

    object TFloat : ServerType<Float>()
    object TDouble : ServerType<Double>()

    object TString : ServerType<String>()
    class TPointer(val table: Table) : ServerType<Pair<Table, String>>()
    class TListPointers(val table: Table) : ServerType<Pair<Table, List<String>>>()
}

data class Property<out S : ServerType<T>, T>(
        val name: String,
        val description: String,
        val type: S,
        val default: T,
        val read: AccessRules = listOf(listOf()),
        val write: AccessRules = listOf(listOf()),
        val versionStart: Int = 0,
        val versionEnd: Int = Int.MAX_VALUE,
        val calculated: ((String, DAO) -> T)? = null
)

sealed class Condition<T> {
    abstract fun test(input: T): Boolean

    data class Equal<out S : ServerType<T>, T>(val property: Property<S, T>, val value: T) : Condition<T>() {
        override fun test(input: T): Boolean = input == value
    }

    data class NotEqual<out S : ServerType<T>, T>(val property: Property<S, T>, val value: T) : Condition<T>() {
        override fun test(input: T): Boolean = input != value
    }

    data class Comparison<out S : ServerType<T>, T : Comparable<T>>(val property: Property<S, T>, val value: T, val lessThan: Boolean = true, val equal: Boolean = false) : Condition<T>() {
        override fun test(input: T): Boolean {
            val result = input.compareTo(value)
            if (result == 0) return equal
            if (lessThan) return result < 0
            else return result > 0
        }
    }

    data class Between<out S : ServerType<T>, T : Comparable<T>>(val property: Property<S, T>, val range: ClosedRange<T>) : Condition<T>() {
        override fun test(input: T): Boolean = range.contains(input)
    }
}

sealed class AccessRule {
    class AccessLevel(val level: Int) : AccessRule()
    class Condition(val condition: Condition)
}

data class Instance(val id: String, val properties: Map<Property<*, *>, Any?>)

interface DAO {
    fun get(id: String, properties: Collection<Property<*, *>>): Instance
    fun set(id: String?, inProperties: Map<Property<*, *>, Any?>, outProperties: Collection<Property<*, *>>): Instance
    fun query(queryProperties: Map<Property<*, *>, Condition<*>>, outProperties: Collection<Property<*, *>>): Collection<Instance>
}

class GraphQLSchemaGenerator(val dao: DAO) {

    val tables = ArrayList<Table>()
    fun table(table: Table) = tables.add(table)

    val schemaBuilder = GraphQLSchema.newSchema()

    val outputTypes = HashMap<Table, GraphQLOutputType>()
    val inputTypes = HashMap<Table, GraphQLInputType>()

    fun generateOutputType(table: Table): GraphQLOutputType {
        val builder = GraphQLObjectType.newObject()
                .name(table.name)
                .description(table.description)
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("ID")
                        .description("The ID of the object")
                        .type(Scalars.GraphQLID)
                        .dataFetcher { environment -> environment.getSource<Instance>().id }
                )
        for (prop in table.properties.values) {
            val def = GraphQLFieldDefinition.newFieldDefinition()
                    .name(prop.name)
                    .description(prop.description)
            val serverType = prop.type
            when (serverType) {
                is ServerType.TPointer -> {
                    val graphQLType = if (serverType.table == table) GraphQLTypeReference(table.name) else serverType.toGraphQLOutputType()
                    def.type(graphQLType)
                            .dataFetcher { environment ->
                                val id = environment.getSource<Instance>().properties[prop] as String?
                                if (id == null) null
                                else {
                                    val properties = environment.fields.mapNotNull { serverType.table.properties[it.name] }
                                    dao.get(id, properties)
                                }
                            }
                }
                is ServerType.TListPointers -> {
                    val graphQLType = if (serverType.table == table) GraphQLTypeReference(table.name) else serverType.toGraphQLOutputType()
                    def.type(GraphQLList.list(graphQLType))
                            .dataFetcher { environment ->
                                @Suppress("UNCHECKED_CAST")
                                val ids = environment.getSource<Instance>().properties[prop] as Collection<String>
                                val properties = environment.fields.mapNotNull { serverType.table.properties[it.name] }
                                ids.map { id -> dao.get(id, properties) }
                            }
                }
                else -> {
                    def.type(prop.type.toGraphQLOutputType())
                            .dataFetcher { environment -> environment.getSource<Instance>().properties[prop] }
                }
            }
            builder.field(def)
        }
        return builder.build()
    }

    fun generateInputType(table: Table): GraphQLInputType {
        val builder = GraphQLInputObjectType.newInputObject()
                .name("Input" + table.name)
                .description(table.description)
        for (prop in table.properties.values) {
            val def = GraphQLInputObjectField.newInputObjectField()
                    .name(prop.name)
                    .description(prop.description)
            val serverType = prop.type
            when (serverType) {
                is ServerType.TPointer -> {
                    val graphQLType = if (serverType.table == table) GraphQLTypeReference(table.name) else serverType.toGraphQLInputType()
                    def.type(graphQLType)
                }
                is ServerType.TListPointers -> {
                    val graphQLType = if (serverType.table == table) GraphQLTypeReference(table.name) else serverType.toGraphQLInputType()
                    def.type(GraphQLList.list(graphQLType))
                }
                else -> {
                    def.type(prop.type.toGraphQLInputType())
                }
            }
            builder.field(def)
        }
        return builder.build()
    }

    fun <T> ServerType<T>.toGraphQLOutputType() = when (this) {
        ServerType.TBoolean -> Scalars.GraphQLBoolean
        ServerType.TByte -> Scalars.GraphQLByte
        ServerType.TShort -> Scalars.GraphQLShort
        ServerType.TInt -> Scalars.GraphQLInt
        ServerType.TLong -> Scalars.GraphQLLong
        ServerType.TFloat -> Scalars.GraphQLFloat
        ServerType.TDouble -> Scalars.GraphQLFloat
        ServerType.TString -> Scalars.GraphQLString
        is ServerType.TPointer -> {
            outputTypes.getOrPut(this.table) {
                generateOutputType(this.table)
            }
        }
        is ServerType.TListPointers -> {
            GraphQLList.list(outputTypes.getOrPut(this.table) {
                generateOutputType(this.table)
            })
        }
    }

    fun <T> ServerType<T>.toGraphQLInputType(): GraphQLInputType = when (this) {
        ServerType.TBoolean -> Scalars.GraphQLBoolean
        ServerType.TByte -> Scalars.GraphQLByte
        ServerType.TShort -> Scalars.GraphQLShort
        ServerType.TInt -> Scalars.GraphQLInt
        ServerType.TLong -> Scalars.GraphQLLong
        ServerType.TFloat -> Scalars.GraphQLFloat
        ServerType.TDouble -> Scalars.GraphQLFloat
        ServerType.TString -> Scalars.GraphQLString
        is ServerType.TPointer -> {
            inputTypes.getOrPut(this.table) {
                generateInputType(this.table)
            }
        }
        is ServerType.TListPointers -> {
            GraphQLList.list(inputTypes.getOrPut(this.table) {
                generateInputType(this.table)
            })
        }
    }

    init {
        schemaBuilder.subscription()
    }

    fun build() = schemaBuilder.build()
}