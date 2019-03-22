package com.hileco.cortex.vm.layer

import java.lang.ref.WeakReference

abstract class BasicLayered<T : BasicLayered<T>>(initialParent: T? = null) {
    protected abstract fun layerExtract(): T
    protected abstract fun layerIsEmpty(): Boolean
    protected abstract fun layerCreate(parent: T?): T
    protected abstract fun layerMergeUpwards()

    private var parent: T?
    private val children: MutableList<WeakReference<T>>

    init {
        var chosenParent = initialParent
        if (chosenParent != null && chosenParent.layerIsEmpty()) {
            chosenParent = chosenParent.parent()
        }
        parent = chosenParent
        children = arrayListOf()
        parent?.children?.add(WeakReference(this as T))
    }

    @Synchronized
    fun branch(): T {
        if (layerIsEmpty()) {
            return layerCreate(parent)
        }
        val currentParent = parent
        currentParent?.children?.removeIf { it.get() === this }
        val newParent = layerExtract()
        newParent.children.add(WeakReference(this as T))
        parent = newParent
        return layerCreate(parent)
    }

    @Synchronized
    fun close() {
        val currentParent = parent
        if (currentParent != null) {
            currentParent.children.removeIf { it.get() === this }
            currentParent.children.singleOrNull()?.get()?.layerMergeUpwards()
        }
    }

    @Synchronized
    fun parent(): T? {
        return parent
    }

    @Synchronized
    fun children(): List<T> {
        return children.mapNotNull { it.get() }.toList()
    }
}
