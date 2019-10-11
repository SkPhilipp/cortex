package com.hileco.cortex.data

interface VmStack<V, T : VmStack<V, T>> : VmComponent<T> {
    operator fun get(index: Int): V

    operator fun set(index: Int, value: V)

    fun peek(offset: Int = 0): V

    fun push(value: V)

    fun pop(): V

    fun swap(topOffsetLeft: Int, topOffsetRight: Int)

    fun duplicate(offset: Int)

    fun isEmpty(): Boolean

    fun clear()

    fun size(): Int

    fun asSequence(): Sequence<V>
}