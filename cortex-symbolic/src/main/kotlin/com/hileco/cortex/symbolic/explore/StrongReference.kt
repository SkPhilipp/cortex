package com.hileco.cortex.analysis.explore

import com.hileco.cortex.collections.VmStack


class StrongReference<T>(val referent: T?) {
    override fun equals(other: Any?): Boolean {
        return other is StrongReference<*> && referent === other.referent
    }

    override fun hashCode(): Int {
        return System.identityHashCode(referent)
    }

    override fun toString(): String {
        if (referent is VmStack<*>) {
            return referent.size().toString()
        }
        return "StrongReference(referent=$referent)"
    }

}