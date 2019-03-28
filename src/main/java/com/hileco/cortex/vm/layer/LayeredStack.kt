package com.hileco.cortex.vm.layer

import java.math.BigInteger

class LayeredStack<V> : TreeLayered<LayeredStack<V>> {
    private var size: Int
    val layer: HashMap<Int, V>

    private constructor(parent: LayeredStack<V>?,
                        size: Int,
                        layer: HashMap<Int, V>) : super(parent) {
        this.size = size
        this.layer = layer
        val mergeableParent = this.parent
        if (mergeableParent != null && mergeableParent.layer.size < MINIMUM_LAYER_SIZE) {
            mergeParent(mergeableParent)
            this.parent = mergeableParent.parent()
        }
    }

    constructor() {
        size = 0
        layer = HashMap()
    }

    fun push(value: V) {
        layer[this.size] = value
        this.size++
    }

    fun pop(): V {
        val value = peek()
        layer.remove(this.size - 1)
        this.size--
        return value
    }

    fun peek(offset: Int = 0): V {
        return if (this.size <= offset) throw IndexOutOfBoundsException("size 0 <= index 0") else this[this.size - offset - 1]
    }

    operator fun get(index: Int): V {
        var chosen: LayeredStack<V>? = this
        while (chosen != null && chosen.size > index) {
            val value = chosen.layer[index]
            if (value != null) {
                return value
            }
            chosen = chosen.parent()
        }
        throw IndexOutOfBoundsException("size ${this.size} <= index $index")
    }

    operator fun set(index: Int, value: V) {
        if (this.size < index) {
            throw IndexOutOfBoundsException("size ${this.size} <= index $index")
        }
        layer[index] = value
    }

    fun swap(topOffsetLeft: Int, topOffsetRight: Int) {
        val leftIndex = (this.size - 1) - topOffsetLeft
        val rightIndex = (this.size - 1) - topOffsetRight
        val left = this[leftIndex]
        val right = this[rightIndex]
        layer[leftIndex] = right
        layer[rightIndex] = left
    }

    fun size(): Int {
        return this.size
    }

    fun isEmpty(): Boolean {
        return this.size == 0
    }

    fun duplicate(topOffset: Int) {
        val value = peek(topOffset)
        push(value)
    }

    fun clear() {
        layer.clear()
        this.size = 0
    }

    @Synchronized
    override fun extractParentLayer(parent: LayeredStack<V>?): LayeredStack<V> {
        val extracted = LayeredStack(parent, size, HashMap(layer))
        layer.clear()
        return extracted
    }

    @Synchronized
    override fun isLayerEmpty(): Boolean {
        return layer.isEmpty()
    }

    @Synchronized
    override fun createSibling(parent: LayeredStack<V>?): LayeredStack<V> {
        return LayeredStack(parent, parent?.size ?: 0, HashMap())
    }

    @Synchronized
    override fun mergeParent(parent: LayeredStack<V>) {
        synchronized(parent) {
            parent.layer.forEach { (key, value) ->
                layer.putIfAbsent(key, value)
            }
        }
    }

    override fun toString(): String {
        return this.asSequence().joinToString(prefix = "[", postfix = "]") { element ->
            if (element is ByteArray) "${BigInteger(element)}" else "$element"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is LayeredStack<*>) {
            if (other.size == this.size) {
                val ownIterator = asSequence().iterator()
                val otherIterator = other.asSequence().iterator()
                while (ownIterator.hasNext()) {
                    if (ownIterator.next() != otherIterator.next()) {
                        return false
                    }
                }
            }
        }
        return true
    }

    override fun hashCode(): Int {
        return size.hashCode()
    }

    fun asSequence() = sequence {
        for (i in 0 until this@LayeredStack.size) {
            yield(this@LayeredStack[i])
        }
    }

    companion object {
        var MINIMUM_LAYER_SIZE: Int = 6
    }
}
