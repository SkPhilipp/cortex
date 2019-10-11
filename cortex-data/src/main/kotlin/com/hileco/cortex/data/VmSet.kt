package com.hileco.cortex.data

interface VmSet<V, T : VmSet<V, T>> : VmComponent<T> {
    fun contains(value: V): Boolean

    fun add(value: V)

    fun remove(value: V)

    fun size(): Int
}