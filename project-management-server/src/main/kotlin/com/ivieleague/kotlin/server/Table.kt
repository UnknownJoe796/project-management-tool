package com.ivieleague.kotlin.server

class Table(val name: String, val description: String) {
    val properties = HashMap<String, Property<*, *>>()
}