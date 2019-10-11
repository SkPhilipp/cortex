package com.hileco.cortex.data

interface VmComponent<T : VmComponent<T>> {
    fun close()

    fun copy(): T
}
