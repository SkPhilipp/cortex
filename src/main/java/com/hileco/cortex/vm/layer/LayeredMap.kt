package com.hileco.cortex.vm.layer

class LayeredMap<K, V>(parent: LayeredMap<K, V>? = null) : TreeLayered<LayeredMap<K, V>>(parent) {
    private var layer: MutableMap<K, V> = HashMap()
    private var deletions: MutableSet<K> = HashSet()

    override fun extractParentLayer(parent: LayeredMap<K, V>?): LayeredMap<K, V> {
        val clone = LayeredMap(parent)
        clone.layer = layer.toMutableMap()
        clone.deletions = deletions.toMutableSet()
        return clone
    }

    override fun isLayerEmpty(): Boolean {
        return layer.isEmpty() && deletions.isEmpty()
    }

    override fun createSibling(): LayeredMap<K, V> {
        return LayeredMap(parent)
    }

    override fun mergeParent() {
        val currentParent = parent
        if (currentParent != null) {
            currentParent.layer.forEach { key, value ->
                if (!deletions.contains(key)) {
                    layer.putIfAbsent(key, value)
                }
            }
            currentParent.deletions.forEach { deletion ->
                if (!layer.containsKey(deletion)) {
                    deletions.add(deletion)
                }
            }
        }
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

    @Synchronized
    operator fun set(key: K, value: V) {
        deletions.remove(key)
        layer[key] = value
    }

    @Synchronized
    fun remove(key: K) {
        layer.remove(key)
        val currentParent = parent
        if (currentParent?.get(key) != null) {
            deletions.add(key)
        }
    }

    @Synchronized
    fun clear() {
        parent = null
        layer.clear()
        deletions.clear()
    }

    @Synchronized
    fun keySet(): Set<K> {
        val keys = HashSet<K>()
        val currentParent = parent
        if (currentParent != null) {
            keys.addAll(currentParent.keySet())
        }
        keys.addAll(layer.keys)
        keys.removeAll(deletions)
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
        return "$stringBuilder"
    }

    override fun equals(other: Any?): Boolean {
        return other is LayeredMap<*, *> && let {
            other as LayeredMap<K, V>
            val keySet = keySet()
            keySet == other.keySet() && keySet.all { key ->
                val thisValue = this[key]
                val otherValue = other[key]
                return (thisValue == null && otherValue == null)
                        || (thisValue != null && thisValue == otherValue)
            }
        }
    }

    override fun hashCode(): Int {
        return keySet().hashCode()
    }
}
