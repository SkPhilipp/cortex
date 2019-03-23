package com.hileco.cortex.vm.layer

import com.hileco.cortex.vm.layer.Layered.Companion.MINIMUM_LAYER_SIZE
import java.math.BigInteger

class LayeredStack<V> : BasicLayered<LayeredStack<V>> {
    private var size: Int
    private var layer: HashMap<Int, V>

    private constructor(parent: LayeredStack<V>?,
                        size: Int = parent?.size ?: 0,
                        layer: HashMap<Int, V> = HashMap()) : super(parent) {
        this.size = size
        this.layer = layer
        val chosenParent = this.parent
        if (chosenParent != null && chosenParent.layer.size < MINIMUM_LAYER_SIZE) {
            mergeParent()
        }
    }

    constructor() {
        size = 0
        layer = HashMap()
    }

    @Synchronized
    fun push(value: V) {
        layer[this.size] = value
        this.size++
    }

    @Synchronized
    fun pop(): V {
        val value = peek()
        layer.remove(this.size - 1)
        this.size--
        return value
    }

    @Synchronized
    fun peek(offset: Int = 0): V {
        return if (this.size <= offset) throw IndexOutOfBoundsException("size 0 <= index 0") else this[this.size - offset - 1]
    }

    @Synchronized
    operator fun get(index: Int): V {
        var chosen: LayeredStack<V>? = this
        while (chosen != null && chosen.size > index) {
            val value = chosen.layer[index]
            if (value != null) {
                return value
            }
            chosen = chosen.parent
        }
        throw IndexOutOfBoundsException("size ${this.size} <= index $index")
    }

    @Synchronized
    operator fun set(index: Int, value: V) {
        if (this.size < index) {
            throw IndexOutOfBoundsException("size ${this.size} <= index $index")
        }
        layer[index] = value
    }

    @Synchronized
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

    @Synchronized
    fun isEmpty(): Boolean {
        return this.size == 0
    }

    @Synchronized
    fun duplicate(topOffset: Int) {
        val value = peek(topOffset)
        push(value)
    }

    @Synchronized
    fun clear() {
        layer.clear()
        this.size = 0
    }

    override fun extractLayer(parent: LayeredStack<V>?): LayeredStack<V> {
        val extracted = LayeredStack(parent, size, layer)
        layer = HashMap()
        return extracted
    }

    override fun isLayerEmpty(): Boolean {
        return layer.isEmpty()
    }

    override fun createEmptyLayer(parent: LayeredStack<V>?): LayeredStack<V> {
        return LayeredStack(parent)
    }

    override fun mergeParent() {
        val currentParent = parent
        if (currentParent != null) {
            layer.forEach { (key, value) ->
                currentParent.layer[key] = value
            }
            layer = currentParent.layer
            parent = currentParent.parent
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
}
