package com.ivieleague.kotlin.server

sealed class Condition<T> {
    abstract fun test(input: T): Boolean

    data class Equal<out S : ServerType<T>, T>(val property: Property<S, T>, val value: T) : Condition<T>() {
        override fun test(input: T): Boolean = input == value
    }

    data class NotEqual<out S : ServerType<T>, T>(val property: Property<S, T>, val value: T) : Condition<T>() {
        override fun test(input: T): Boolean = input != value
    }

    data class Comparison<out S : ServerType<T>, T : Comparable<T>>(val property: Property<S, T>, val value: T, val lessThan: Boolean = true, val equal: Boolean = false) : Condition<T>() {
        override fun test(input: T): Boolean {
            val result = input.compareTo(value)
            if (result == 0) return equal
            if (lessThan) return result < 0
            else return result > 0
        }
    }

    data class Between<out S : ServerType<T>, T : Comparable<T>>(val property: Property<S, T>, val range: ClosedRange<T>) : Condition<T>() {
        override fun test(input: T): Boolean = range.contains(input)
    }
}