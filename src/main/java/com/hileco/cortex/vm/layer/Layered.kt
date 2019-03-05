package com.hileco.cortex.vm.layer

interface Layered<T : Layered<T>> {
    fun branch(): T
    fun close()

    companion object {
        const val MINIMUM_LAYER_SIZE: Int = 2
    }
}
