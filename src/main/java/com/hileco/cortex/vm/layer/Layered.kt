package com.hileco.cortex.vm.layer

interface Layered<T : Layered<T>> {
    fun branch(): T
    fun close()
}
