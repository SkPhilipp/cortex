package com.hileco.cortex.vm.layer.test

import com.hileco.cortex.vm.layer.Layered

class LayeredNavigator {
    fun root(start: Layered<*>): Layered<*> {
        var currentParent = start
        do {
            val parent = currentParent.parent()
            if (parent != null) {
                currentParent = parent
            }
        } while (parent != null)
        return currentParent
    }

    fun depth(start: Layered<*>): Int {
        var depth = 0
        var current = start
        do {
            val parent = current.parent()
            if (parent != null) {
                current = parent
                depth++
            }
        } while (parent != null)
        return depth
    }

    fun remainingDepth(start: Layered<*>): Int {
        var current = listOf(start)
        var depth = 0
        while (current.isNotEmpty()) {
            depth++
            current = current.flatMap { it.children() }
        }
        return depth - 1
    }
}