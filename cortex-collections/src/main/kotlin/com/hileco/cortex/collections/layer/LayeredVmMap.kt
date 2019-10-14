package com.hileco.cortex.collections.layer

import com.hileco.cortex.collections.base.BaseVmMap

class LayeredVmMap<K, V> : BaseVmMap<K, V> {
    var edge: MapLayer<K, V>

    private constructor(edge: MapLayer<K, V>) {
        this.edge = edge
    }

    constructor() : this(MapLayer<K, V>(null))

    override fun get(key: K): V? {
        return get(edge, key)
    }

    private operator fun get(layer: MapLayer<K, V>, key: K): V? {
        if (layer.entries.containsKey(key)) {
            return layer.entries[key]
        }
        if (layer.deletions.contains(key)) {
            return null
        }
        val parent = layer.parent ?: return null
        return get(parent, key)
    }

    override fun keySet(): Set<K> {
        val entries = HashSet<K>()
        val deletions = HashSet<K>()
        var currentEdge: MapLayer<K, V>? = this.edge
        while (currentEdge != null) {
            deletions.addAll(currentEdge.deletions.filter { !entries.contains(it) })
            entries.addAll(currentEdge.entries.map { it.key }.filter { !deletions.contains(it) })
            currentEdge = currentEdge.parent
        }
        return entries
    }

    override fun set(key: K, value: V) {
        edge.deletions.remove(key)
        edge.entries[key] = value
    }

    override fun remove(key: K) {
        edge.deletions.add(key)
        edge.entries.remove(key)
    }

    override fun copy(): LayeredVmMap<K, V> {
        while (edge.isEmpty) {
            this.edge = edge.parent ?: return LayeredVmMap()
        }
        val child1 = MapLayer(edge)
        val child2 = MapLayer(edge)
        this.edge = child1
        return LayeredVmMap(child2)
    }

    override fun close() {
        edge.close()
    }
}
