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
    protected abstract fun createSibling(parent: T?): T

    /**
     * merges the parent layer into this layer
     */
    protected abstract fun mergeParent(parent: T)

    private var parent: T?
    private val children: MutableList<WeakReference<T>>

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
            val sibling = createSibling(parent)
            sibling.parent?.children?.add(WeakReference(sibling))
            return sibling
        }
        val newParent = extractParentLayer(parent)
        val sibling = createSibling(newParent)
        newParent.children.add(WeakReference(this as T))
        sibling.parent?.children?.add(WeakReference(sibling))
        parent?.children?.add(WeakReference(newParent))
        parent?.children?.removeIf { it.get() === this }
        parent = newParent
        return sibling
    }

    @Synchronized
    final override fun dispose() {
        val currentParent = parent
        if (currentParent != null) {
            currentParent.children.removeIf { it.get() === this || it.get() === null }
            currentParent.children.singleOrNull()?.get()?.let { lastSibling ->
                lastSibling.mergeParent(currentParent)
                lastSibling.parent?.parent?.children?.add(WeakReference(lastSibling))
                lastSibling.parent?.children?.removeIf { it.get() === lastSibling }
                lastSibling.parent = lastSibling.parent?.parent
            }
            if (currentParent.children.size == 0) {
                currentParent.dispose()
            }
        }
    }

    @Synchronized
    final override fun parent(): T? {
        return parent
    }

    @Synchronized
    final override fun children(): List<T> {
        return children.mapNotNull { it.get() }.toList()
    }
}
