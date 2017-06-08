package com.ivieleague.kotlin.server

import com.ivieleague.kotlin.PropertyDelegate
import com.ivieleague.kotlin.SimplePropertyDelegate

object RawPDF : PropertyDelegateFactory {
    override fun <T> make(property: ServerType.TObject.Property<T, ServerType<T>>, default: T): PropertyDelegate<T> = SimplePropertyDelegate(default)
}