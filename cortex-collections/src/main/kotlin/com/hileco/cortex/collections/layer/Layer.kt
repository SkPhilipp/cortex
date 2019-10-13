package com.hileco.cortex.collections.layer

import java.util.*

/**
 * [Layer] structures' internal layering must be thread-safe.
 *
 * Objects implementing [Layer] do not have to be thread safe
 */
@Suppress("UNCHECKED_CAST")
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
        currentParent?.addChild(this as T)
    }

    @Synchronized
    private fun addChild(child: T) {
        this.children.add(child)
    }

    @Synchronized
    private fun removeChild(child: T): Boolean {
        this.children.remove(child)
        return this.children.isEmpty()
    }

    fun close() {
        var currentParent = this.parent
        while (currentParent != null) {
            val removedLastChild = currentParent.removeChild(this as T)
            if (removedLastChild) {
                currentParent = currentParent.parent
            } else {
                break
            }
        }
        this.parent = null
    }
}
