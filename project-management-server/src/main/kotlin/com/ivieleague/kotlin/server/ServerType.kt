package com.ivieleague.kotlin.server

import com.ivieleague.kotlin.PropertyDelegate

typealias AccessRules = List<List<ServerType.TObject.AccessRule>>

sealed class ServerType<T> {

    object TId : ServerType<String?>()

    object TByte : ServerType<Byte>()
    object TShort : ServerType<Short>()
    object TInt : ServerType<Int>()
    object TLong : ServerType<Long>()

    object TFloat : ServerType<Float>()
    object TDouble : ServerType<Double>()

    object TString : ServerType<String>()

    class TPointer<SUB>(val subtype: ServerType<SUB>) : ServerType<Link<SUB>>()
    class TList<SUB>(val subtype: ServerType<SUB>) : ServerType<List<SUB>>()

    abstract class TObject<KOT : TObject.Instance>(val typeName: String) : ServerType<KOT>(), Set<TObject.Property<*, ServerType<*>>> {
        abstract fun instance(factory: PropertyDelegateFactory): KOT

        val id: Property<String?, TId> = property("id", "The ID of the object", ServerType.TId, null, listOf(listOf()), listOf(listOf()))
        val set = HashSet<Property<*, ServerType<*>>>()

        override val size: Int get() = set.size
        override fun contains(element: Property<*, ServerType<*>>): Boolean = set.contains(element)
        override fun containsAll(elements: Collection<Property<*, ServerType<*>>>): Boolean = set.containsAll(elements)
        override fun isEmpty(): Boolean = set.isEmpty()
        override fun iterator(): Iterator<Property<*, ServerType<*>>> = set.iterator()

        interface Instance {
            val id: String?
            val properties: Map<Property<*, *>, PropertyDelegate<*>>
        }

        abstract class InstanceImpl(val idProperty: Property<String?, TId>, val factory: PropertyDelegateFactory) : Instance {
            override val properties = HashMap<Property<*, *>, PropertyDelegate<*>>()
            inline fun <reified T, S : ServerType<T>> property(property: Property<T, S>, default: T) = factory.make(property, default)

            override var id by property(idProperty, null)
        }

        fun <T, S : ServerType<T>> property(
                name: String,
                description: String,
                type: S,
                default: T,
                read: AccessRules,
                write: AccessRules
        ) = Property<T, S>(
                name = name,
                description = description,
                type = type,
                default = default,
                read = read,
                write = write
        ).also {
            set += it
        }

        data class Property<T, out S : ServerType<T>>(
                val name: String,
                val description: String,
                val type: S,
                val default: T,
                val read: AccessRules = listOf(listOf()),
                val write: AccessRules = listOf(listOf()),
                val versionStart: Int = 0,
                val versionEnd: Int = Int.MAX_VALUE,
                val calculated: ((Instance) -> T)? = null
        )

        sealed class AccessRule {
            class AccessLevel(val level: Int) : AccessRule()
            class PropertyEqual(val myProperty: Property<*, *>, val userProperty: Property<*, *>)
            class PropertyIn(val myProperty: Property<*, *>, val userProperty: Property<TList<*>, *>)
            class PropertyContains(val myProperty: Property<TList<*>, *>, val userProperty: Property<*, *>)
        }
    }
}