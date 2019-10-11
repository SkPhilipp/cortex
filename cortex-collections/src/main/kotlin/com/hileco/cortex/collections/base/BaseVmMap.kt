package com.hileco.cortex.collections.base

import com.hileco.cortex.collections.VmMap

@Suppress("UNCHECKED_CAST")
abstract class BaseVmMap<K, V> : VmMap<K, V> {
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
        return other is VmMap<*, *> && let {
            other as VmMap<K, *>
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