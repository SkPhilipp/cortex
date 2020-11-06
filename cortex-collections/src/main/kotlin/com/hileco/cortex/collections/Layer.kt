package com.hileco.cortex.collections

import java.util.*
import java.util.concurrent.atomic.AtomicLong

/**
 * [Layer] structures' internal layering must be thread-safe.
 *
 * Objects implementing [Layer] do not have to be thread safe
 */
@Suppress("UNCHECKED_CAST")
abstract class Layer<T : Layer<T>>(parent: T?) {
    val id by lazy { ATOMIC_ID.incrementAndGet() }
    private val layerChildren: MutableList<T>
    var layerParent: T? = null
        private set

    internal abstract val isLayerEmpty: Boolean

    init {
        this.layerChildren = ArrayList()
        var currentParent = parent
        while (currentParent != null && currentParent.isLayerEmpty) {
            currentParent = currentParent.layerParent
        }
        this.layerParent = currentParent
        currentParent?.addChild(this as T)
    }

    @Synchronized
    private fun addChild(child: T) {
        this.layerChildren.add(child)
    }

    @Synchronized
    private fun removeChild(child: T): Boolean {
        this.layerChildren.remove(child)
        return this.layerChildren.isEmpty()
    }

    @Synchronized
    fun changeParent(newParent: T?) {
        val currentParent = layerParent
        if (newParent == currentParent) {
            return
        }
        if (currentParent != null) {
            val removedLastChild = currentParent.removeChild(this as T)
            if (removedLastChild) {
                currentParent.close()
            }
        }
        layerParent = newParent
        layerParent?.addChild(this as T)
    }

    fun close() {
        var currentParent = this.layerParent
        while (currentParent != null) {
            val removedLastChild = currentParent.removeChild(this as T)
            if (removedLastChild) {
                currentParent = currentParent.layerParent
            } else {
                break
            }
        }
        this.layerParent = null
    }

    override fun equals(other: Any?): Boolean {
        return other is Layer<*> && id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        private val ATOMIC_ID = AtomicLong(0)
    }
}
