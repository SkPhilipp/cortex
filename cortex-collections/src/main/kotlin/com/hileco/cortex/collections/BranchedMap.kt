package com.hileco.cortex.collections

class BranchedMap<K, V> : Branched<BranchedMap<K, V>> {
    var edge: MapLayer<K, V>

    private constructor(edge: MapLayer<K, V>) {
        this.edge = edge
    }

    constructor() : this(MapLayer<K, V>(null))

    operator fun get(key: K): V? {
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

    fun keySet(): Set<K> {
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

    operator fun set(key: K, value: V) {
        edge.deletions.remove(key)
        edge.entries[key] = value
    }

    fun remove(key: K) {
        edge.deletions.add(key)
        edge.entries.remove(key)
    }

    fun size(): Int {
        return keySet().size
    }

    override fun copy(): BranchedMap<K, V> {
        while (edge.isEmpty) {
            this.edge = edge.parent ?: return BranchedMap()
        }
        val child1 = MapLayer(edge)
        val child2 = MapLayer(edge)
        this.edge = child1
        return BranchedMap(child2)
    }

    override fun close() {
        edge.close()
    }

    override fun toString(): String {
        return keySet().joinToString(separator = ", ", prefix = "{", postfix = "}") { "[${it}] = ${this[it]},\n" }
    }

    override fun hashCode(): Int {
        return keySet().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is BranchedMap<*, *> && let {
            other as BranchedMap<K, *>
            val keySet = keySet()
            keySet.all { key ->
                val thisValue = this[key]
                val otherValue = other[key]
                return (thisValue == null && otherValue == null)
                        || (thisValue != null && thisValue == otherValue)
            }
        }
    }
}
