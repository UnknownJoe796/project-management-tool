package com.ivieleague.kotlin.server

typealias AccessRules = List<List<AccessRule>>
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