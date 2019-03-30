package com.hileco.cortex.vm.layer

import java.lang.ref.WeakReference

/**
 * Base class for structures containing other [Layered] structures.
 *
 * Provides standard [Layered] operation, does not support [TreeLayered]-style optimizations.
 */
abstract class DelegateLayered<T : DelegateLayered<T>> : Layered<T> {
    /**
     * Reconstructs a parent [T] based on all delegate branched structures' parents.
     */
    protected abstract fun recreateParent(): T

    /**
     * Branches all delegate [Layered] structures, and returns a new [T] based on the branched sturctures.
     */
    protected abstract fun branchDelegates(): T

    /**
     * Closes all delegate [Layered] structures
     */
    protected abstract fun disposeDelegates()

    private var parent: T?
    private val children: MutableList<WeakReference<T>>

    init {
        parent = null
        children = arrayListOf()
    }

    @Synchronized
    private fun addChild(child: T) {
        this.children.add(WeakReference(child))
    }

    @Synchronized
    private fun removeChild(child: T?) {
        this.children.removeIf { it.get() === child }
    }

    final override fun branch(): T {
        val sibling = branchDelegates()
        val newParent = recreateParent()
        newParent.parent = parent
        newParent.addChild(this as T)
        newParent.addChild(sibling)
        parent?.addChild(newParent)
        parent?.removeChild(this)
        sibling.parent = newParent
        parent = newParent
        return sibling
    }

    @Synchronized
    final override fun dispose() {
        disposeDelegates()
        val currentParent = parent
        if (currentParent != null) {
            currentParent.removeChild(this as T)
            currentParent.removeChild(null)
            if (currentParent.children().isEmpty()) {
                currentParent.dispose()
            }
        }
    }

    final override fun parent(): T? {
        return parent
    }

    @Synchronized
    final override fun children(): List<T> {
        return children.mapNotNull { it.get() }.toList()
    }
}
