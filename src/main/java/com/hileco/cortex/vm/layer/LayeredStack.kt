package com.hileco.cortex.vm.layer

import java.lang.ref.WeakReference
import java.math.BigInteger

class LayeredStack<V> : Layered<LayeredStack<V>> {
    private var parent: LayeredStack<V>?
    private var size: Int
    private var layer: HashMap<Int, V>
    private val children: MutableList<WeakReference<LayeredStack<V>>>

    private constructor(parent: LayeredStack<V>?,
                        size: Int = parent?.size ?: 0,
                        layer: HashMap<Int, V> = HashMap()) {
        var chosenParent = parent
        while (chosenParent != null && chosenParent.layer.size == 0) {
            chosenParent = chosenParent.parent
        }
        this.parent = chosenParent
        chosenParent?.children?.add(WeakReference(this))
        this.size = size
        this.layer = layer
        children = arrayListOf()
    }

    constructor() {
        parent = null
        size = parent?.size ?: 0
        layer = HashMap()
        children = arrayListOf()
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
    fun peek(): V {
        return if (this.size == 0) throw IndexOutOfBoundsException("size 0 <= index 0") else this[this.size - 1]
    }

    @Synchronized
    override fun branch(): LayeredStack<V> {
        val currentParent = parent
        currentParent?.children?.removeIf { it.get() === this }
        val newParent = LayeredStack(currentParent, size, layer)
        newParent.children.add(WeakReference(this))
        parent = newParent
        layer = HashMap()
        return LayeredStack(newParent)
    }

    @Synchronized
    override fun close() {
        val currentParent = parent
        if (currentParent != null) {
            currentParent.children.removeIf { it.get() === this }
            currentParent.children.singleOrNull()?.get()?.mergeWithParent()
        }
    }

    @Synchronized
    private fun mergeWithParent() {
        val currentParent = parent
        if (currentParent != null) {
            layer.forEach { (key, value) ->
                currentParent.layer[key] = value
            }
            layer = currentParent.layer
            parent = currentParent.parent
        }
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
        return size.hashCode()
    }

    fun asSequence() = sequence {
        for (i in 0 until this@LayeredStack.size) {
            yield(this@LayeredStack[i])
        }
    }
}
