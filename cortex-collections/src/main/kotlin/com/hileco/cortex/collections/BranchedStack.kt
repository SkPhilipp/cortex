package com.hileco.cortex.collections


class BranchedStack<V> : Branched<BranchedStack<V>> {
    var edge: StackLayer<V>

    private constructor(edge: StackLayer<V>) {
        this.edge = edge
    }

    constructor() {
        this.edge = StackLayer(null)
    }

    fun push(value: V) {
        this.edge.entries[this.edge.length] = value
        this.edge.length++
    }

    fun pop(): V {
        val value = peek()
        this.edge.entries.remove(this.edge.length - 1)
        this.edge.length--
        return value
    }

    fun peek(offset: Int = 0): V {
        return this[this.edge.length - offset - 1]
    }

    operator fun get(index: Int): V {
        var chosen: StackLayer<V>? = this.edge
        while (chosen != null && chosen.length > index) {
            val value = chosen.entries[index]
            if (value != null) {
                return value
            }
            chosen = chosen.parent
        }
        throw IndexOutOfBoundsException("size ${this.edge.length} <= index $index")
    }

    operator fun set(index: Int, value: V) {
        if (this.edge.length < index) {
            throw IndexOutOfBoundsException("size ${this.edge.length} <= index $index")
        }
        this.edge.entries[index] = value
    }

    fun size(): Int {
        return this.edge.length
    }

    fun clear() {
        this.edge.entries.clear()
        this.edge.length = 0
    }

    override fun close() {
        this.edge.close()
    }

    override fun copy(): BranchedStack<V> {
        while (edge.isEmpty) {
            this.edge = edge.parent ?: return BranchedStack()
        }
        val child1 = StackLayer(edge)
        val child2 = StackLayer(edge)
        this.edge = child1
        return BranchedStack(child2)
    }

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

    override fun toString(): String {
        return this.asSequence().joinToString(prefix = "[", postfix = "]") { element ->
            if (element is ByteArray) element.serialize() else "$element"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is BranchedStack<*>) {
            if (other.size() == this.size()) {
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
        return size().hashCode()
    }

    fun asSequence() = sequence {
        val size = size()
        for (i in 0 until size) {
            yield(peek(i))
        }
    }
}
