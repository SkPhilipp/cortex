package com.hileco.cortex.analysis

import com.hileco.cortex.instructions.Instruction

inline fun <T> Iterable<T>.forEachTwo(action: (T, T) -> Unit) {
    val iterator = this.iterator()
    if (iterator.hasNext()) {
        var first = iterator.next()
        while (iterator.hasNext()) {
            val second = iterator.next()
            action(first, second)
            first = second
        }
    }
}

/**
 * Creates a GraphNode from this and [that].
 */
infix fun Int.to(that: Instruction): GraphNode = GraphNode(that, this)
