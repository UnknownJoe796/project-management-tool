package com.ivieleague.kotlin.server

interface DAO {
    fun get(id: String, properties: Collection<Property<*, *>>): Instance
    fun set(id: String?, inProperties: Map<Property<*, *>, Any?>, outProperties: Collection<Property<*, *>>): Instance
    fun query(queryConditions: List<Condition<*>>, outProperties: Collection<Property<*, *>>): Collection<Instance>
}