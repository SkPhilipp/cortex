package com.hileco.cortex.collections

import java.util.*

class MapLayer<K, V>(parent: MapLayer<K, V>?) : Layer<MapLayer<K, V>>(parent) {
    val entries: MutableMap<K, V> = HashMap()
    val deletions: MutableSet<K> = HashSet()

    override val isEmpty: Boolean
        get() = entries.isEmpty() && deletions.isEmpty()

    fun contentEquals(other: MapLayer<K, V>): Boolean {
        return entries == other.entries && deletions == other.deletions
    }

    fun overwrites(key: K): Boolean {
        return entries.containsKey(key) || deletions.contains(key)
    }
}
