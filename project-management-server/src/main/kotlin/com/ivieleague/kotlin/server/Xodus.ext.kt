package com.ivieleague.kotlin.server

import com.ivieleague.kotlin.PropertyDelegate
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityIterable
import jetbrains.exodus.entitystore.StoreTransaction
import kotlin.reflect.KProperty

class XodusPDF(val transaction: StoreTransaction, val entity: Entity) : PropertyDelegateFactory {
    @Suppress("UNCHECKED_CAST")
    override fun <T> make(property: ServerType.TObject.Property<T, ServerType<T>>, default: T): PropertyDelegate<T> {
        val type = property.type
        return when (type) {
            ServerType.TId -> TODO()
            ServerType.TByte -> XodusProperty<Byte>(transaction, entity, property.name, property.default as Byte) as PropertyDelegate<T>
            ServerType.TShort -> XodusProperty<Short>(transaction, entity, property.name, property.default as Short) as PropertyDelegate<T>
            ServerType.TInt -> XodusProperty<Int>(transaction, entity, property.name, property.default as Int) as PropertyDelegate<T>
            ServerType.TLong -> XodusProperty<Long>(transaction, entity, property.name, property.default as Long) as PropertyDelegate<T>
            ServerType.TFloat -> XodusProperty<Float>(transaction, entity, property.name, property.default as Float) as PropertyDelegate<T>
            ServerType.TDouble -> XodusProperty<Double>(transaction, entity, property.name, property.default as Double) as PropertyDelegate<T>
            ServerType.TString -> XodusProperty<String>(transaction, entity, property.name, property.default as String) as PropertyDelegate<T>
            is ServerType.TPointer<*> -> {
                val subtype = type.subtype
                if (subtype is ServerType.TObject) {
                    XodusLinkProperty(transaction, entity, property.name, subtype as ServerType.TObject<ServerType.TObject.Instance>) as PropertyDelegate<T>
                } else throw IllegalArgumentException("Pointers to non-objects not supported yet")
            }
            is ServerType.TList<*> -> {
                val subtype = type.subtype
                if (subtype is ServerType.TPointer<*>) {
                    val subbertype = type.subtype
                    if (subbertype is ServerType.TObject) {
                        XodusLinksProperty(transaction, entity, property.name, subbertype as ServerType.TObject<ServerType.TObject.Instance>) as PropertyDelegate<T>
                    } else throw IllegalArgumentException("Pointers to non-objects not supported yet")
                } else throw IllegalArgumentException("Non-links not supported yet")
            }
            is ServerType.TObject -> throw IllegalArgumentException("Direct objects not supported yet")
        }
    }
}

class XodusProperty<T : Comparable<*>>(val transaction: StoreTransaction, val entity: Entity, val key: String, val default: T) : PropertyDelegate<T> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return entity.getProperty(key) as? T ?: default
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        entity.setProperty(key, value)
    }
}

class XodusLink<T : ServerType.TObject.Instance>(val transaction: StoreTransaction, val entity: Entity?, val serverType: ServerType.TObject<T>) : Link<T> {
    override fun follow(): T? {
        return entity?.let { serverType.instance(XodusPDF(transaction, it)) }
    }
}

class XodusLinkProperty<T : ServerType.TObject.Instance>(val transaction: StoreTransaction, val entity: Entity, val key: String, val serverType: ServerType.TObject<T>) : PropertyDelegate<Link<T>> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Link<T> = XodusLink(transaction, entity.getLink(key), serverType)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Link<T>) {
        if (value is XodusLink) {
            entity.setLink(key, value.entity)
        } else {
            val actual = value.follow() ?: return
            val id = actual.id
            if (id == null) {
                //Create a new one!
                val ent = transaction.newEntity(serverType.typeName)
                entity.setLink(key, ent)
            } else {
                //Link an existing one!
                val ent = transaction.getEntity(transaction.toEntityId(id))
                entity.setLink(key, ent)
            }

            throw IllegalArgumentException()
        }
    }
}

class XodusLinksProperty<T : ServerType.TObject.Instance>(val transaction: StoreTransaction, val entity: Entity, val key: String, val serverType: ServerType.TObject<T>) : PropertyDelegate<MutableCollection<Link<T>>> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): MutableCollection<Link<T>> = MutableXodusEntityIterable(transaction, entity, key, serverType)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: MutableCollection<Link<T>>) {
        entity.deleteLinks(key)
        value.forEach {
            it.getXodusEntity(transaction, serverType)?.let { entity.addLink(key, it) }
        }
    }
}

class XodusEntityIterable<T : ServerType.TObject.Instance>(
        val transaction: StoreTransaction,
        val underlying: EntityIterable,
        val serverType: ServerType.TObject<T>
) : Collection<Link<T>> {

    override val size: Int get() = underlying.size().toInt()
    override fun contains(element: Link<T>): Boolean = element.getXodusEntity(transaction, serverType)?.let { underlying.contains(it) } ?: false
    override fun containsAll(elements: Collection<Link<T>>): Boolean = elements.all { contains(it) }

    override fun isEmpty(): Boolean = underlying.isEmpty

    override fun iterator(): MutableIterator<Link<T>> = object : MutableIterator<Link<T>> {
        val iter = underlying.iterator()
        override fun hasNext(): Boolean = iter.hasNext()
        override fun next(): Link<T> = XodusLink(transaction, iter.next(), serverType)
        override fun remove() = iter.remove()
    }
}

class MutableXodusEntityIterable<T : ServerType.TObject.Instance>(
        val transaction: StoreTransaction,
        val entity: Entity,
        val key: String,
        val serverType: ServerType.TObject<T>
) : MutableCollection<Link<T>> {
    val underlying: EntityIterable = entity.getLinks(key)
    override val size: Int get() = underlying.size().toInt()
    override fun contains(element: Link<T>): Boolean = element.getXodusEntity(transaction, serverType)?.let { underlying.contains(it) } ?: false
    override fun containsAll(elements: Collection<Link<T>>): Boolean = elements.all { contains(it) }

    override fun isEmpty(): Boolean = underlying.isEmpty

    override fun iterator(): MutableIterator<Link<T>> = object : MutableIterator<Link<T>> {
        val iter = underlying.iterator()
        override fun hasNext(): Boolean = iter.hasNext()
        override fun next(): Link<T> = XodusLink(transaction, iter.next(), serverType)
        override fun remove() = iter.remove()
    }

    override fun add(element: Link<T>): Boolean = element.getXodusEntity(transaction, serverType)?.let { entity.addLink(key, it) } ?: false
    override fun addAll(elements: Collection<Link<T>>): Boolean = elements.all { add(it) }
    override fun clear() {
        entity.deleteLinks(key)
    }

    override fun remove(element: Link<T>): Boolean = element.getXodusEntity(transaction, serverType)?.let { entity.deleteLink(key, it) } ?: false
    override fun removeAll(elements: Collection<Link<T>>): Boolean = elements.all { remove(it) }
    override fun retainAll(elements: Collection<Link<T>>): Boolean = throw UnsupportedOperationException()
}


private fun <T : ServerType.TObject.Instance> T.getXodusEntity(transaction: StoreTransaction, serverType: ServerType.TObject<T>): Entity {
    val id = this.id
    if (id == null) {
        //Create a new one!
        return transaction.newEntity(serverType.typeName)
    } else {
        //Link an existing one!
        return transaction.getEntity(transaction.toEntityId(id))
    }
}

private fun <T : ServerType.TObject.Instance> Link<T>.getXodusEntity(transaction: StoreTransaction, serverType: ServerType.TObject<T>): Entity? {
    val linked = follow() ?: return null
    return linked.getXodusEntity(transaction, serverType)
}