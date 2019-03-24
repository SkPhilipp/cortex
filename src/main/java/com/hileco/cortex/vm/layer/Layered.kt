package com.hileco.cortex.vm.layer

interface Layered<T : Layered<T>> {
    fun parent(): T

    fun children(): List<T>

    fun root(): T

    fun branch(): T

    fun dispose()
}
