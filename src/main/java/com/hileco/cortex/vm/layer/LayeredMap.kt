package com.hileco.cortex.vm.layer

class LayeredMap<K, V> {
    private var parent: LayeredMap<K, V>?
    private var layer: MutableMap<K, V>
    private var deletions: MutableSet<K>

    constructor() {
        parent = null
        layer = HashMap()
        deletions = HashSet()
    }

    private constructor(parent: LayeredMap<K, V>, layer: MutableMap<K, V>, deletions: MutableSet<K>) {
        this.parent = parent
        this.layer = layer
        this.deletions = deletions
    }

    @Synchronized
    fun spawnChild(): LayeredMap<K, V> {
        return LayeredMap(this, HashMap(), HashSet())
    }

    fun size(): Int {
        return keySet().size
    }

    @Synchronized
    operator fun get(key: K): V? {
        if (layer.containsKey(key)) {
            return layer[key]
        }
        return if (!deletions.contains(key)) {
            return parent?.get(key)
        } else null
    }

    operator fun set(key: K, value: V) {
        deletions.remove(key)
        layer[key] = value
    }

    fun clear() {
        parent = null
        layer.clear()
        deletions.clear()
    }

    fun keySet(): Set<K> {
        val keys = HashSet<K>()
        if (parent != null) {
            keys.addAll(parent!!.keySet())
        }
        keys.addAll(layer.keys)
        if (parent != null) {
            keys.removeAll(deletions)
        }
        return keys
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("LayeredMap{")
        stringBuilder.append("\n")
        keySet().forEach { key ->
            stringBuilder.append("[")
            stringBuilder.append(key)
            stringBuilder.append("] = ")
            stringBuilder.append(this[key])
            stringBuilder.append(",")
            stringBuilder.append("\n")
        }
        stringBuilder.append("}")
        return stringBuilder.toString()
    }
}
