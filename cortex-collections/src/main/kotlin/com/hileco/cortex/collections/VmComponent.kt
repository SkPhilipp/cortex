package com.hileco.cortex.collections

interface VmComponent<T : VmComponent<T>> {
    fun close()

    fun copy(): T
}
