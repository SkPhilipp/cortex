package com.hileco.cortex.data.backed


import com.hileco.cortex.data.base.BaseVmMap
import java.util.*

class BackedVmMap<K, V> : BaseVmMap<K, V, BackedVmMap<K, V>> {
    private var backingMap: MutableMap<K, V>

    constructor() {
        this.backingMap = HashMap()
    }

    private constructor(backingMap: MutableMap<K, V>) {
        this.backingMap = HashMap(backingMap)
    }

    override fun close() {}

    override fun copy(): BackedVmMap<K, V> {
        return BackedVmMap(backingMap)
    }

    override fun get(key: K): V? {
        return backingMap[key]
    }

    override fun set(key: K, value: V) {
        backingMap[key] = value
    }

    override fun remove(key: K) {
        backingMap.remove(key)
    }

    override fun keySet(): Set<K> {
        return backingMap.keys
    }
}
