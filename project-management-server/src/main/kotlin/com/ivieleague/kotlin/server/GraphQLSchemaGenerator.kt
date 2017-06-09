package com.ivieleague.kotlin.server

import com.lightningkite.kotlin.collection.Cache
import graphql.Scalars
import graphql.schema.*

class GraphQLSchemaGenerator(val dao: DAO) {

    val tables = ArrayList<Table>()
    fun table(table: Table) = tables.add(table)

    val schemaBuilder = GraphQLSchema.newSchema()

    val outputTypes = Cache<Table, GraphQLOutputType> {
        generateOutputType(it)
    }
    val inputTypes = Cache<Table, GraphQLInputType> {
        generateInputType(it)
    }

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
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("id")
                        .description("The ID of the object.")
                        .type(Scalars.GraphQLID)
                )
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
            outputTypes[this.table]
        }
        is ServerType.TListPointers -> {
            GraphQLList.list(outputTypes[this.table])
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
            inputTypes[this.table]!!
        }
        is ServerType.TListPointers -> {
            GraphQLList.list(inputTypes[this.table])
        }
    }

    fun build() = schemaBuilder.apply {
        query(GraphQLObjectType.newObject()
                .name("QueryType")
                .apply {
                    for (table in tables) {
                        field(GraphQLFieldDefinition.newFieldDefinition()
                                .name(table.name)
                                .description(table.description)
                                .type(outputTypes[table])
                                .argument(GraphQLArgument.newArgument()
                                        .name("id")
                                        .description("The ID of the object")
                                        .type(GraphQLNonNull.nonNull(Scalars.GraphQLID))
                                )
                                .dataFetcher { environment ->
                                    val outProperties = environment.fields.mapNotNull { table.properties[it.name] }
                                    dao.get(
                                            id = environment.arguments["id"] as String,
                                            properties = outProperties
                                    )
                                }
                        )
                    }
                    for (table in tables) {
                        field(GraphQLFieldDefinition.newFieldDefinition()
                                .name("Query" + table.name)
                                .description(table.description)
                                .type(GraphQLList.list(outputTypes[table]))
                                .apply {
                                    for (property in table.properties.values) {
                                        argument(GraphQLArgument.newArgument()
                                                .name(property.name)
                                                .description(property.description)
                                                .type(property.type.toGraphQLInputType())
                                        )
                                    }
                                }
                                .dataFetcher { environment ->
                                    //TODO: More conditions than equals
                                    @Suppress("UNCHECKED_CAST")
                                    val queryConditions = environment.arguments.entries.map {
                                        Condition.Equal<ServerType<Any?>, Any?>(table.properties[it.key] as Property<ServerType<Any?>, Any?>, it.value)
                                    }
                                    val outProperties = environment.fields.mapNotNull { table.properties[it.name] }
                                    dao.query(
                                            queryConditions = queryConditions,
                                            outProperties = outProperties
                                    )
                                }
                        )
                    }
                }
        )
        mutation(GraphQLObjectType.newObject()
                .name("MutationType")
                .apply {
                    for (table in tables) {
                        field(GraphQLFieldDefinition.newFieldDefinition()
                                .name(table.name)
                                .description(table.description)
                                .type(outputTypes[table])
                                .argument(GraphQLArgument.newArgument()
                                        .name("id")
                                        .description("The ID of the object")
                                        .type(Scalars.GraphQLID)
                                )
                                .apply {
                                    for (property in table.properties.values) {
                                        argument(GraphQLArgument.newArgument()
                                                .name(property.name)
                                                .description(property.description)
                                                .type(property.type.toGraphQLInputType())
                                        )
                                    }
                                }
                                .dataFetcher { environment ->
                                    val outProperties = environment.fields.mapNotNull { table.properties[it.name] }
                                    val arguments = environment.arguments
                                    recursiveSet(table, arguments, outProperties)
                                }
                        )
                    }
                }
        )
    }.build()

    private fun recursiveSet(
            table: Table,
            arguments: Map<String, Any?>,
            outProperties: List<Property<*, *>>
    ): Instance {
        val id = arguments["id"] as? String
        val inProperties = arguments.entries.asSequence()
                .mapNotNull { entry -> table.properties[entry.key]?.let { it to entry.value } }
                .associate {
                    val property = it.first
                    val type = property.type
                    when (type) {
                        is ServerType.TListPointers -> property to (it.second as List<Map<String, Any?>>).map { recursiveSet(type.table, it, listOf()) }
                        is ServerType.TPointer -> property to recursiveSet(type.table, it.second as Map<String, Any?>, listOf())
                        else -> property to it.second
                    }
                }
        return dao.set(
                id = id,
                inProperties = inProperties,
                outProperties = outProperties
        )
    }
}