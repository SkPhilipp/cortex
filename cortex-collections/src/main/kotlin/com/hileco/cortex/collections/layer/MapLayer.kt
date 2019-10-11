package com.hileco.cortex.collections.layer

import java.util.*

class MapLayer<K, V>(parent: MapLayer<K, V>?) : Layer<MapLayer<K, V>>(parent) {
    val entries: MutableMap<K, V> = HashMap()
    val deletions: MutableSet<K> = HashSet()

    override val isEmpty: Boolean
        get() = entries.isEmpty() && deletions.isEmpty()
}
