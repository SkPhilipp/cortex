package com.hileco.cortex.vm.layer

import java.lang.ref.WeakReference

abstract class BasicLayered<T : BasicLayered<T>>(initialParent: T? = null) {
    protected abstract fun extractLayer(parent: T?): T
    protected abstract fun isLayerEmpty(): Boolean
    protected abstract fun createEmptyLayer(parent: T?): T
    protected abstract fun mergeParent()

    var parent: T?
    val children: MutableList<WeakReference<T>>

    init {
        var chosenParent = initialParent
        if (chosenParent != null && chosenParent.isLayerEmpty()) {
            chosenParent = chosenParent.parent
        }
        parent = chosenParent
        children = arrayListOf()
        parent?.children?.add(WeakReference(this as T))
    }

    @Synchronized
    fun branch(): T {
        if (isLayerEmpty()) {
            return createEmptyLayer(parent)
        }
        val currentParent = parent
        currentParent?.children?.removeIf { it.get() === this }
        val newParent = extractLayer(parent)
        newParent.children.add(WeakReference(this as T))
        parent = newParent
        return createEmptyLayer(parent)
    }

    @Synchronized
    fun close() {
        val currentParent = parent
        if (currentParent != null) {
            currentParent.children.removeIf { it.get() === this }
            currentParent.children.singleOrNull()?.get()?.mergeParent()
        }
    }

    @Synchronized
    fun children(): List<T> {
        return children.mapNotNull { it.get() }.toList()
    }

    @Synchronized
    fun root(): T? {
        var currentParent: T? = parent
        while (currentParent != null) {
            currentParent = currentParent.parent
        }
        return currentParent
    }
}
