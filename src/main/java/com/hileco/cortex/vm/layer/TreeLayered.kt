package com.hileco.cortex.vm.layer

import java.lang.ref.WeakReference

abstract class TreeLayered<T : TreeLayered<T>>(initialParent: T? = null) : Layered<T>, Navigable<T> {
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

    var parent: T?
    val children: MutableList<WeakReference<T>>

    init {
        var chosenParent = initialParent
        if (chosenParent != null && chosenParent.isLayerEmpty()) {
            chosenParent = chosenParent.parent
        }
        parent = chosenParent
        children = arrayListOf()
    }

    @Synchronized
    override fun branch(): T {
        if (isLayerEmpty()) {
            val sibling = createSibling()
            parent?.children?.add(WeakReference(sibling))
            return sibling
        }
        val currentParent = parent
        currentParent?.children?.removeIf { it.get() === this }
        val newParent = extractParentLayer(parent)
        newParent.children.add(WeakReference(this as T))
        parent = newParent
        val sibling = createSibling()
        newParent.children.add(WeakReference(sibling))
        return sibling
    }

    @Synchronized
    override fun close() {
        val currentParent = parent
        if (currentParent != null) {
            currentParent.children.removeIf { it.get() === this }
            currentParent.children.singleOrNull()?.get()?.let { lastSibling ->
                lastSibling.mergeParent()
                currentParent.children.clear()
                lastSibling.parent?.children?.add(WeakReference(lastSibling))
            }
            if (currentParent.children.size == 0) {
                currentParent.close()
            }
        }
    }

    @Synchronized
    override fun parent(): T? {
        return parent
    }

    @Synchronized
    override fun children(): List<T> {
        return children.mapNotNull { it.get() }.toList()
    }

    @Synchronized
    override fun root(): T? {
        var currentParent: T? = parent
        while (currentParent?.parent != null) {
            currentParent = currentParent.parent
        }
        return currentParent
    }
}
