package com.hileco.cortex.vm.layer

interface Layered<T : Layered<T>> : AutoCloseable {
    fun branch(): T

    fun finalize() {
        close()
    }
}
