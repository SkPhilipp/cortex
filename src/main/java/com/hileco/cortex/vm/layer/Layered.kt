package com.hileco.cortex.vm.layer

/**
 * [Layered] structures must be thread-safe.
 *
 * To ensure thread-safety when implementing [Layered], follow these rules:
 * - Method should likely be [Synchronized]
 * - When changing internal parent or children references; add first, then remove
 * - When merging with parent; do not modify the parent & attach first, then detatch
 */
interface Layered<T : Layered<T>> {
    fun parent(): T?

    fun children(): List<T>

    fun branch(): T

    fun dispose()
}
