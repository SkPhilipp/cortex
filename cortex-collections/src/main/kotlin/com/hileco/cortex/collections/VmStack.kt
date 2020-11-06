package com.hileco.cortex.collections

interface VmStack<V> : VmComponent<VmStack<V>> {
    operator fun get(index: Int): V

    operator fun set(index: Int, value: V)

    fun peek(offset: Int = 0): V

    fun push(value: V)

    fun pop(): V

    fun swap(topOffsetLeft: Int, topOffsetRight: Int) {
        val size = size()
        val indexLeft = size - topOffsetLeft - 1
        val indexRight = size - topOffsetRight - 1
        val left = get(indexLeft)
        val right = get(indexRight)
        set(indexLeft, right)
        set(indexRight, left)
    }

    fun duplicate(offset: Int) {
        val value = peek(offset)
        push(value)
    }

    fun isEmpty(): Boolean {
        return size() == 0
    }

    fun clear()

    fun size(): Int

    fun asSequence(): Sequence<V>
}