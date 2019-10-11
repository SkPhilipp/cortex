package com.hileco.cortex.data.layer

import com.hileco.cortex.data.VmSet

class LayeredVmSet<T> private constructor(private var edge: SetLayer<T>) : VmSet<T, LayeredVmSet<T>> {

    constructor() : this(SetLayer<T>(null))

    override fun contains(value: T): Boolean {
        return contains(edge, value)
    }

    private fun contains(layer: SetLayer<T>, value: T): Boolean {
        if (layer.entries.contains(value)) {
            return true
        }
        if (layer.deletions.contains(value)) {
            return false
        }
        val parent = layer.parent ?: return false
        return contains(parent, value)
    }

    override fun add(value: T) {
        if (!contains(value)) {
            edge.deletions.remove(value)
            edge.entries.add(value)
        }
    }

    override fun remove(value: T) {
        if (contains(value)) {
            edge.deletions.add(value)
            edge.entries.remove(value)
        }
    }

    override fun size(): Int {
        throw UnsupportedOperationException()
    }

    override fun copy(): LayeredVmSet<T> {
        while (edge.isEmpty) {
            this.edge = edge.parent ?: return LayeredVmSet()
        }
        val child1 = SetLayer(edge)
        val child2 = SetLayer(edge)
        this.edge = child1
        return LayeredVmSet(child2)
    }

    override fun close() {
        edge.close()
    }
}
