package com.ivieleague.kotlin.web

import org.w3c.dom.Node

inline fun <reified T> Any?.cast(): T = this as T

fun String.htmlEscape(): String = this
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("&", "&amp;")
        .replace("\"", "&quot;")
        .replace("\'", "&apos;")

fun Node.childIterator() = object : Iterator<Node> {

    var current = this@childIterator.firstChild

    override fun hasNext(): Boolean = current != null

    override fun next(): Node {
        val result = current ?: throw IllegalStateException("HEY")
        current = result.nextSibling
        return result
    }
}

fun Node.childSequence() = object : Sequence<Node> {
    override fun iterator(): Iterator<Node> = childIterator()
}