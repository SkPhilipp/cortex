package com.hileco.cortex.collections.layer

import java.util.*

/**
 * [Layer] structures' internal layering must be thread-safe.
 *
 * Objects implementing [Layer] do not have to be thread safe
 *
 * To ensure proper thread-safety when implementing [Layer], follow these rules:
 * - Methods modifying internal layering structure must be [Synchronized]
 * - When changing internal parent or children references; add first, then remove
 * - When merging with parent; do not modify the parent & attach first, then detatch
 */
abstract class Layer<T : Layer<T>>(parent: T?) {
    val children: MutableList<T>
    var parent: T? = null

    internal abstract val isEmpty: Boolean

    init {
        this.children = ArrayList()
        var currentParent = parent
        while (currentParent != null && currentParent.isEmpty) {
            currentParent = currentParent.parent
        }
        this.parent = currentParent
        currentParent?.children?.add(this as T)
    }

    fun close() {
        var currentParent = this.parent
        while (currentParent != null) {
            currentParent.children.remove(this)
            if (currentParent.children.isEmpty()) {
                currentParent = currentParent.parent
            }
        }
        this.parent = null
    }
}
