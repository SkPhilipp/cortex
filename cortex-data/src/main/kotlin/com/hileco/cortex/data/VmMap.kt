package com.hileco.cortex.data

interface VmMap<K, V, T : VmMap<K, V, T>> : VmComponent<T> {
    operator fun get(key: K): V?

    operator fun set(key: K, value: V)

    fun remove(key: K)

    fun size(): Int

    fun keySet(): Set<K>
}