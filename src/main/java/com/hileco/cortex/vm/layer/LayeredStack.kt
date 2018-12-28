package com.hileco.cortex.vm.layer

import java.util.*
import kotlin.collections.HashMap

class LayeredStack<V>(private val parent: LayeredStack<V>? = null) {
    private var size: Int = parent?.size ?: 0
    private val layer: MutableMap<Int, V?> = HashMap()

    @Synchronized
    fun push(value: V) {
        this.size++
        layer[this.size] = value
    }

    @Synchronized
    fun pop(): V? {
        if (this.size == 0) {

        }
        val value = peek()
        layer.remove(this.size)
        this.size--
        return value
    }

    @Synchronized
    fun peek(): V? {
        return if (this.size == 0) null else this[this.size]

    }

    @Synchronized
    private fun checkBounds(index: Int) {
        if (this.size < index) {
            throw IndexOutOfBoundsException("size ${this.size} < index $index")
        }
    }

    @Synchronized
    operator fun get(index: Int): V? {
        checkBounds(index)
        return if (layer.containsKey(index)) {
            layer[index]
        } else {
            if (parent == null) {
                throw IndexOutOfBoundsException("size ${this.size} < index $index")
            } else {
                parent[index]
            }
        }
    }

    @Synchronized
    operator fun set(index: Int, value: V?) {
        checkBounds(index)
        layer[index] = value
    }

    @Synchronized
    fun swap(topOffsetLeft: Int, topOffsetRight: Int) {
        val leftIndex = this.size - topOffsetLeft
        val rightIndex = this.size - topOffsetRight
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
        return layer.isEmpty() && (parent == null || parent.size == 0)
    }

    @Synchronized
    fun duplicate(topOffset: Int) {
        val value = this[this.size - topOffset]!!
        push(value)
    }

    @Synchronized
    operator fun contains(element: Int): Boolean {
        return layer.containsKey(element) || parent != null && parent.contains(element)
    }

    operator fun iterator(): IndexedIterator {
        return IndexedIterator(1)
    }

    @Synchronized
    fun add(v: V): Boolean {
        push(v)
        return true
    }

    @Synchronized
    fun clear() {
        layer.clear()
        this.size = 0
    }

    fun listIterator(): ListIterator<V> {
        return IndexedIterator(1)
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("LayeredStack{")
        val iterator = this.iterator()
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next())
            if (iterator.hasNext()) {
                stringBuilder.append(", ")
            }
        }
        stringBuilder.append("}")
        return stringBuilder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other is LayeredStack<*>) {
            if (other.size == this.size) {
                val ownIterator = listIterator()
                val otherIterator = other.listIterator()
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

    inner class IndexedIterator constructor(private var index: Int) : ListIterator<V> {

        @Synchronized
        override fun hasNext(): Boolean {
            return index <= this@LayeredStack.size
        }

        @Synchronized
        override fun next(): V {
            if (index > this@LayeredStack.size) {
                throw NoSuchElementException()
            }
            val value = this@LayeredStack[index]
            index++
            return value!!
        }

        override fun hasPrevious(): Boolean {
            return index > 0
        }

        override fun previous(): V {
            if (index == 0) {
                throw NoSuchElementException()
            }
            index--
            return this@LayeredStack[index]!!
        }

        override fun nextIndex(): Int {
            return Math.min(index, this@LayeredStack.size - 1)
        }

        override fun previousIndex(): Int {
            return index - 1
        }
    }
}
