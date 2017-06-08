package com.ivieleague.kotlin.server

import com.ivieleague.kotlin.PropertyDelegate

interface PropertyDelegateFactory {
    fun <T> make(property: ServerType.TObject.Property<T, ServerType<T>>, default: T): PropertyDelegate<T>
}