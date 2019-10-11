package com.hileco.cortex.collections

interface VmSet<V> : VmComponent<VmSet<V>> {
    fun contains(value: V): Boolean

    fun add(value: V)

    fun remove(value: V)

    fun size(): Int
}