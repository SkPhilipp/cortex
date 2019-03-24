package com.hileco.cortex.vm.layer

import java.lang.ref.WeakReference

abstract class TreeLayered<T : TreeLayered<T>>(initialParent: T? = null) : Layered<T> {
    /**
     * Creates a new [T] instance representing a copy of this layer, to be the new parent.
     * Clears this layer as such that [isLayerEmpty] is true.
     *
     * @return the new instance
     */
    protected abstract fun extractParentLayer(parent: T?): T

    /**
     * @return true when this is an empty layer
     */
    abstract fun isLayerEmpty(): Boolean

    /**
     * @return a sibling, whose layer is empty
     */
    protected abstract fun createSibling(): T

    /**
     * merges the parent layer into this layer
     */
    protected abstract fun mergeParent()

    protected var parent: T?
    protected val children: MutableList<WeakReference<T>>

    init {
        var chosenParent = initialParent
        if (chosenParent != null && chosenParent.isLayerEmpty()) {
            chosenParent = chosenParent.parent
        }
        parent = chosenParent
        children = arrayListOf()
    }

    @Synchronized
    final override fun branch(): T {
        if (isLayerEmpty()) {
            val sibling = createSibling()
            parent?.children?.add(WeakReference(sibling))
            return sibling
        }
        val currentParent = parent
        val newParent = extractParentLayer(parent)
        parent = newParent
        val sibling = createSibling()
        currentParent?.children?.removeIf { it.get() === this }
        currentParent?.children?.add(WeakReference(newParent))
        newParent.children.add(WeakReference(this as T))
        newParent.children.add(WeakReference(sibling))
        return sibling
    }

    @Synchronized
    final override fun dispose() {
        val currentParent = parent
        if (currentParent != null) {
            currentParent.children.removeIf { it.get() === this || it.get() === null }
            currentParent.children.singleOrNull()?.get()?.let { lastSibling ->
                lastSibling.mergeParent()
                lastSibling.parent?.children?.removeIf { it.get() === lastSibling }
                lastSibling.parent = lastSibling.parent?.parent
                lastSibling.parent?.children?.add(WeakReference(lastSibling))
            }
            if (currentParent.children.size == 0) {
                currentParent.dispose()
            }
        }
    }

    @Synchronized
    final override fun parent(): T {
        return parent ?: this as T
    }

    @Synchronized
    final override fun children(): List<T> {
        return children.mapNotNull { it.get() }.toList()
    }

    @Synchronized
    final override fun root(): T {
        var currentParent: T = parent()
        while (currentParent.parent() != currentParent) {
            currentParent = currentParent.parent()
        }
        return currentParent
    }
}
