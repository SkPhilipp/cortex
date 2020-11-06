package com.hileco.cortex.collections


class LayeredVmStack<V> : VmStack<V> {
    var edge: StackLayer<V>

    private constructor(edge: StackLayer<V>) {
        this.edge = edge
    }

    constructor() {
        this.edge = StackLayer(null)
    }

    override fun push(value: V) {
        this.edge.entries[this.edge.length] = value
        this.edge.length++
    }

    override fun pop(): V {
        val value = peek()
        this.edge.entries.remove(this.edge.length - 1)
        this.edge.length--
        return value
    }

    override fun peek(offset: Int): V {
        return this[this.edge.length - offset - 1]
    }

    override operator fun get(index: Int): V {
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

    override operator fun set(index: Int, value: V) {
        if (this.edge.length < index) {
            throw IndexOutOfBoundsException("size ${this.edge.length} <= index $index")
        }
        this.edge.entries[index] = value
    }

    override fun size(): Int {
        return this.edge.length
    }

    override fun clear() {
        this.edge.entries.clear()
        this.edge.length = 0
    }

    override fun close() {
        this.edge.close()
    }

    override fun copy(): LayeredVmStack<V> {
        while (edge.isEmpty) {
            this.edge = edge.parent ?: return LayeredVmStack()
        }
        val child1 = StackLayer(edge)
        val child2 = StackLayer(edge)
        this.edge = child1
        return LayeredVmStack(child2)
    }

    override fun toString(): String {
        return this.asSequence().joinToString(prefix = "[", postfix = "]") { element ->
            if (element is ByteArray) element.serialize() else "$element"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is VmStack<*>) {
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

    override fun asSequence() = sequence {
        val size = size()
        for (i in 0 until size) {
            yield(peek(i))
        }
    }
}
