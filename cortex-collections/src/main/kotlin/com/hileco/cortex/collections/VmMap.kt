package com.hileco.cortex.collections

interface VmMap<K, V> : VmComponent<VmMap<K, V>> {
    operator fun get(key: K): V?

    operator fun set(key: K, value: V)

    fun remove(key: K)

    fun size(): Int {
        return keySet().size
    }

    fun keySet(): Set<K>
}