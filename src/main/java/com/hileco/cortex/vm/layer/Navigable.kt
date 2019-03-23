package com.hileco.cortex.vm.layer

interface Navigable<T : Navigable<T>> {
    fun parent(): T?
    fun children(): List<T>
    fun root(): T?
}
