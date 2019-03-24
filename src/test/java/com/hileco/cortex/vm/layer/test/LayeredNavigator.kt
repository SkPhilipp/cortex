package com.hileco.cortex.vm.layer.test

import com.hileco.cortex.vm.layer.Layered

class LayeredNavigator {
    fun depth(start: Layered<*>): Int {
        var depth = 0
        var current = start
        while (current.parent() !== current) {
            current = current.parent()
            depth++
        }
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