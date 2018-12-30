package com.hileco.cortex.vm.layer

class LayeredMap<K, V>(private var parent: LayeredMap<K, V>? = null) {
    private var layer: MutableMap<K, V> = HashMap()
    private var deletions: MutableSet<K> = HashSet()

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

    fun remove(key: K) {
        layer.remove(key)
        val currentParent = parent
        if (currentParent?.get(key) != null) {
            deletions.add(key)
        }
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
