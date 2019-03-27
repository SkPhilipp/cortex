package com.hileco.cortex.vm.layer

/**
 * [Layered] structures' internal layering must be thread-safe.
 *
 * Objects implementing [Layered] do not have to be thread safe
 *
 * To ensure proper thread-safety when implementing [Layered], follow these rules:
 * - Methods modifying internal layering structure must be [Synchronized]
 * - When changing internal parent or children references; add first, then remove
 * - When merging with parent; do not modify the parent & attach first, then detatch
 */
interface Layered<T : Layered<T>> {
    fun parent(): T?

    fun children(): List<T>

    fun branch(): T

    fun dispose()
}
