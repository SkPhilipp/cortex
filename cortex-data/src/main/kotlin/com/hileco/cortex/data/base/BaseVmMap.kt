package com.hileco.cortex.data.base

import com.hileco.cortex.data.VmMap

abstract class BaseVmMap<K, V, T : BaseVmMap<K, V, T>> : VmMap<K, V, T> {
    override fun size(): Int {
        return keySet().size
    }

    override fun toString(): String {
        return keySet().joinToString(separator = ", ", prefix = "{", postfix = "}") { "[${it}] = ${this[it]},\n" }
    }

    override fun hashCode(): Int {
        return keySet().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is VmMap<*, *, *> && let {
            other as VmMap<K, *, *>
            val keySet = keySet()
            keySet.all { key ->
                val thisValue = this[key]
                val otherValue = other[key]
                return (thisValue == null && otherValue == null)
                        || (thisValue != null && thisValue == otherValue)
            }
        }
    }
}