package com.ivieleague.kotlin.server

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLOutputType

fun ServerType<*>.toGraphQLOutputType(): GraphQLOutputType = TODO()

fun GraphQLObjectType.Builder.field(property: ServerType.TObject.Property<*, *>) {
    field(GraphQLFieldDefinition.newFieldDefinition()
            .name(property.name)
            .description(property.description)
            .type(property.type.toGraphQLOutputType())
            .dataFetcher { environment -> environment.getSource<ServerType.TObject.Instance>().properties[property] }
    )
}

//import kotlin.reflect.KClass
//import kotlin.reflect.KType
//import kotlin.reflect.full.memberProperties
//import kotlin.reflect.jvm.jvmErasure
//
////Generate queries, mutators, types, and input types
//
//val DefaultFieldNameMapper = { it:String -> it.replace(Regex("[A-Z]"), { "_" + it.value.toLowerCase() })}
//val ScalarTypes = mapOf<KClass<*>, GraphQLScalarType>(
//        String::class to Scalars.GraphQLString,
//        Integer::class to Scalars.GraphQLInt,
//        Long::class to Scalars.GraphQLLong,
//        Char::class to Scalars.GraphQLChar,
//        Float::class to Scalars.GraphQLFloat,
//        Double::class to Scalars.GraphQLFloat,
//        Boolean::class to Scalars.GraphQLBoolean
//)
//
//class GraphQLSchemaGenerator(){
//    val nameMapper = DefaultFieldNameMapper
//
//    val interfaces = HashMap<KClass<*>, GraphQLInterfaceType>()
//    val outputTypes = HashMap<KClass<*>, GraphQLOutputType>(ScalarTypes)
//    val inputTypes = HashMap<KClass<*>, GraphQLInputType>(ScalarTypes)
//
//
//    fun getOutputType(type: KType):GraphQLOutputType{
//        val erased = type.jvmErasure
//        val subresult = when(erased){
//            List::class -> GraphQLList.list(getOutputType(type.arguments.first().type!!))
//            else -> outputTypes.getOrPut(erased){ generateFromClass(erased) }
//        }
//        return if(type.isMarkedNullable) subresult else GraphQLNonNull.nonNull(subresult)
//    }
//    fun getInterface(kType: KType):GraphQLInterfaceType{
//        val erasure = kType.jvmErasure
//        val quick = interfaces[erasure]
//        if(quick != null) return quick
//        generateInterface(erasure)
//        return interfaces[erasure]!!
//    }
//
//    fun <T:Any> generateInterface(kclass: KClass<T>):GraphQLInterfaceType{
//        val result:GraphQLInterfaceType
//
//        interfaces[kclass] = result
//        return result
//    }
//
//    fun <T:Any> generateFromClass(kclass: KClass<T>):GraphQLOutputType = GraphQLObjectType.newObject()
//            .name(kclass.simpleName)
//            .description("Generated")
//            .apply{
//                for(x in kclass.supertypes){
//                    if(x.jvmErasure in interfaces){
//                        withInterface(getInterface(x))
//                    }
//                }
//                for(field in kclass.memberProperties){
//                    field(GraphQLFieldDefinition.newFieldDefinition()
//                            .name(field.name)
//                            .description("Generated")
//                            .type(getOutputType(field.returnType))
//                            .dataFetcher { it.getContext(). }
//                    )
//                }
//            }
//            .build()
//}