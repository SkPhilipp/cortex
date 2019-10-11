package com.hileco.cortex.data.layer

import java.util.HashMap
import java.util.HashSet

class MapLayer<K, V>(parent: MapLayer<K, V>?) : Layer<MapLayer<K, V>>(parent) {
    val entries: MutableMap<K, V> = HashMap()
    val deletions: MutableSet<K> = HashSet()

    override val isEmpty: Boolean
        get() = entries.isEmpty() && deletions.isEmpty()
}
