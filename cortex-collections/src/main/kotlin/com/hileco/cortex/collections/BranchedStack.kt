package com.hileco.cortex.collections

import java.util.*

private class StackLayer<T>(parent: StackLayer<T>?) : Layer<StackLayer<T>>(parent) {
    val entries: MutableMap<Int, T> = HashMap()
    var length: Int = 0

    override val isLayerEmpty: Boolean
        get() {
            return entries.isEmpty() && layerParent?.length == length
        }

    init {
        this.length = parent?.length ?: 0
    }

    fun push(value: T) {
        entries[length] = value
        length++
    }

    fun pop(): T {
        val value = peek()
        entries.remove(length - 1)
        length--
        return value
    }

    fun peek(offset: Int = 0): T {
        return this[length - offset - 1]
    }

    operator fun get(index: Int): T {
        var layer: StackLayer<T>? = this
        while (layer != null && layer.length > index) {
            val value = layer.entries[index]
            if (value != null) {
                return value
            }
            layer = layer.layerParent
        }
        throw IndexOutOfBoundsException("size $length <= index $index")
    }

    operator fun set(index: Int, value: T) {
        if (length < index) {
            throw IndexOutOfBoundsException("size $length <= index $index")
        }
        this.entries[index] = value
    }

    fun asSequence(): Sequence<T> {
        return IntRange(0, length - 1)
                .asSequence()
                .map { this[it] }
    }
}

/**
 * Implements [Collection] and [Branched] using a backing [StackLayer] tree.
 *
 * Note that [Branched] implementations are explicitly not thread unsafe.
 *
 * Please avoid the following methods when performance is vital for your use case;
 * - [contains]
 * - [containsAll]
 * - [iterator]
 *
 * [BranchedStack] diverges from regular collections:
 * - [equals] and [hashCode] use only [Layer.id], they do not represent or compare the content of the structure itself
 */
class BranchedStack<T> : Collection<T>, Branched<BranchedStack<T>> {
    private var edge: StackLayer<T>

    private constructor(edge: StackLayer<T>) {
        this.edge = edge
    }

    constructor() {
        this.edge = StackLayer(null)
    }

    override val size: Int
        get() = edge.length

    override fun contains(element: T): Boolean {
        return edge.asSequence().any { it == element }
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return edge.asSequence().all { elements.contains(it) }
    }

    override fun isEmpty(): Boolean {
        return edge.length == 0
    }

    override fun iterator(): Iterator<T> {
        return edge.asSequence().iterator()
    }

    fun push(value: T) {
        this.edge.push(value)
    }

    fun pop(): T {
        return this.edge.pop()
    }

    fun peek(offset: Int = 0): T {
        return this.edge.peek(offset)
    }

    operator fun get(index: Int): T {
        return this.edge[index]
    }

    operator fun set(index: Int, value: T) {
        this.edge[index] = value
    }

    fun swap(topOffsetLeft: Int, topOffsetRight: Int) {
        val indexLeft = edge.length - topOffsetLeft - 1
        val indexRight = edge.length - topOffsetRight - 1
        val left = get(indexLeft)
        val right = get(indexRight)
        set(indexLeft, right)
        set(indexRight, left)
    }

    fun duplicate(offset: Int) {
        val value = peek(offset)
        push(value)
    }

    fun clear() {
        this.edge.close()
        this.edge = StackLayer(null)
    }

    override fun close() {
        this.edge.close()
    }

    override fun copy(): BranchedStack<T> {
        while (edge.isLayerEmpty) {
            this.edge = edge.layerParent ?: return BranchedStack()
        }
        val child1 = StackLayer(edge)
        val child2 = StackLayer(edge)
        this.edge = child1
        return BranchedStack(child2)
    }

    override fun parent(): BranchedStack<T>? {
        val edgeLayerParent = edge.layerParent
        if (edgeLayerParent == null) {
            return null
        } else {
            return BranchedStack(edgeLayerParent)
        }
    }

    override fun id(): Long {
        return edge.id
    }

    override fun hashCode(): Int {
        return edge.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is BranchedStack<*> && other.edge == edge
    }
}
