package com.hileco.cortex.vm.layer

import java.math.BigInteger
import java.util.*
import kotlin.collections.HashMap

class LayeredStack<V>(private val parent: LayeredStack<V>? = null) {
    private var size: Int = parent?.size ?: 0
    private val layer: HashMap<Int, V> = HashMap()

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
    fun peek(): V {
        return if (this.size == 0) throw IndexOutOfBoundsException("size 0 <= index 0") else this[this.size - 1]
    }

    @Synchronized
    operator fun get(index: Int): V {
        return layer[index] ?: if (parent == null) {
            throw IndexOutOfBoundsException("size ${this.size} <= index $index")
        } else {
            parent[index]
        }
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
        val value = this[(this.size - 1) - topOffset]
        push(value)
    }

    @Synchronized
    fun clear() {
        layer.clear()
        this.size = 0
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("LayeredStack{")
        stringBuilder.append(this.asSequence().joinToString(", ") { element ->
            if (element is ByteArray) "${BigInteger(element)}" else "$element"
        })
        stringBuilder.append("}")
        return "$stringBuilder"
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
        return Objects.hash(layer, parent)
    }

    fun asSequence() = sequence {
        for (i in 0 until this@LayeredStack.size) {
            yield(this@LayeredStack[i])
        }
    }
}
