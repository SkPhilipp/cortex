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
    final override fun branch(): T {
        val sibling = branchDelegates()
        val newParent = recreateParent()
        newParent.parent = parent
        newParent.children.add(WeakReference(this as T))
        newParent.children.add(WeakReference(sibling))
        parent?.children?.add(WeakReference(newParent))
        parent?.children?.removeIf { it.get() === this }
        sibling.parent = newParent
        parent = newParent
        return sibling
    }

    @Synchronized
    final override fun dispose() {
        disposeDelegates()
        val currentParent = parent
        if (currentParent != null) {
            currentParent.children.removeIf { it.get() === this || it.get() === null }
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
