package com.hileco.cortex.collections

import java.util.*

private class MapLayer<K, V>(parent: MapLayer<K, V>?) : Layer<MapLayer<K, V>>(parent) {

    val entries: MutableMap<K, V> = HashMap()
    val deletions: MutableSet<K> = HashSet()

    override val isLayerEmpty: Boolean
        get() = entries.isEmpty() && deletions.isEmpty()

    operator fun get(key: K): V? {
        if (entries.containsKey(key)) {
            return entries[key]
        }
        if (deletions.contains(key)) {
            return null
        }
        val parent = layerParent ?: return null
        return parent[key]
    }

    fun keys(): MutableSet<K> {
        val entries = HashSet<K>()
        val deletions = HashSet<K>()
        var edge: MapLayer<K, V> = this
        while (true) {
            deletions.addAll(edge.deletions.filter { !entries.contains(it) })
            entries.addAll(edge.entries.map { it.key }.filter { !deletions.contains(it) })
            edge = edge.layerParent ?: break
        }
        return entries
    }

    operator fun set(key: K, value: V) {
        deletions.remove(key)
        entries[key] = value
    }

    fun remove(key: K) {
        deletions.add(key)
        entries.remove(key)
    }
}

private class BranchedMapEntry<K, V>(override val key: K,
                                     override var value: V) : MutableMap.MutableEntry<K, V> {
    override fun setValue(newValue: V): V {
        val previousValue = value
        value = newValue
        return previousValue
    }
}

/**
 * Implements [MutableMap] and [Branched] using a backing [MapLayer] tree.
 *
 * Note that [Branched] implementations are explicitly not thread unsafe.
 *
 * Please avoid the following methods when performance is vital for your use case;
 * - [keys]
 * - [size]
 * - [values]
 * - [containsValue]
 * - [isEmpty]
 *
 * [BranchedMap] diverges from regular collections:
 * - Operations returning their previous value instead always return null (includes at least [put] and [remove].)
 * - [equals] and [hashCode] use only [Layer.id], they do not represent or compare the content of the structure itself
 */
class BranchedMap<K : Any, V : Any> : MutableMap<K, V>, Branched<BranchedMap<K, V>> {

    private var edge: MapLayer<K, V>

    private constructor(edge: MapLayer<K, V>) {
        this.edge = edge
    }

    constructor() : this(MapLayer<K, V>(null))

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = keys.asSequence().mapNotNull { key ->
            val value = this[key]
            if (value == null) {
                null
            } else {
                BranchedMapEntry(key, value)
            }
        }.toMutableSet()

    override val keys: MutableSet<K>
        get() = edge.keys()

    override val size: Int
        get() = keys.size

    override val values: MutableCollection<V>
        get() = keys.asSequence().mapNotNull { key -> this[key] }.toMutableList()

    override fun containsKey(key: K): Boolean {
        return edge[key] != null
    }

    override fun containsValue(value: V): Boolean {
        return values.contains(value)
    }

    override fun get(key: K): V? {
        return edge[key]
    }

    override fun isEmpty(): Boolean {
        return keys.isEmpty()
    }

    override fun clear() {
        edge.close()
        edge = MapLayer(null)
    }

    override fun put(key: K, value: V): V? {
        edge[key] = value
        return null
    }

    override fun putAll(from: Map<out K, V>) {
        from.forEach { (key, value) ->
            put(key, value)
        }
    }

    override fun remove(key: K): V? {
        edge.remove(key)
        return null
    }

    override fun close() {
        edge.close()
    }

    override fun copy(): BranchedMap<K, V> {
        val child1 = MapLayer(edge)
        val child2 = MapLayer(edge)
        this.edge = child1
        return BranchedMap(child2)
    }

    override fun parent(): BranchedMap<K, V>? {
        val edgeLayerParent = edge.layerParent
        if (edgeLayerParent == null) {
            return null
        } else {
            return BranchedMap(edgeLayerParent)
        }
    }

    override fun id(): Long {
        return edge.id
    }

    override fun toString(): String {
        return keys.joinToString(separator = ", ", prefix = "{", postfix = "}") { "[${it}] = ${this[it]},\n" }
    }

    override fun hashCode(): Int {
        return edge.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is BranchedMap<*, *> && other.edge == edge
    }
}
