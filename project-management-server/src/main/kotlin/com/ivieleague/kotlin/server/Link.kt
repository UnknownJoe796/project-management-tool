package com.ivieleague.kotlin.server

interface Link<T> {
    fun follow(): T?
}

class SimpleLink<T>(var other: T?) : Link<T> {
    override fun follow(): T? = other
}