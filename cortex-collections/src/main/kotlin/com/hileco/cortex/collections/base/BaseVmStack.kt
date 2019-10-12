package com.hileco.cortex.collections.base

import com.hileco.cortex.collections.VmStack
import java.math.BigInteger

abstract class BaseVmStack<V> : VmStack<V> {

    override fun swap(topOffsetLeft: Int, topOffsetRight: Int) {
        val size = size()
        val indexLeft = size - topOffsetLeft - 1
        val indexRight = size - topOffsetRight - 1
        val left = get(indexLeft)
        val right = get(indexRight)
        set(indexLeft, right)
        set(indexRight, left)
    }

    override fun duplicate(offset: Int) {
        val value = peek(offset)
        push(value)
    }

    override fun isEmpty(): Boolean {
        return size() == 0
    }

    override fun toString(): String {
        return this.asSequence().joinToString(prefix = "[", postfix = "]") { element ->
            if (element is ByteArray) "${BigInteger(element)}" else "$element"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is VmStack<*>) {
            if (other.size() == this.size()) {
                val ownIterator = asSequence().iterator()
                val otherIterator = other.asSequence().iterator()
                while (ownIterator.hasNext()) {
                    if (ownIterator.next() != otherIterator.next()) {
                        return false
                    }
                }
            }
        }
        return true
    }

    override fun hashCode(): Int {
        return size().hashCode()
    }

    override fun asSequence() = sequence {
        val size = size()
        for (i in 0 until size) {
            yield(peek(i))
        }
    }
}