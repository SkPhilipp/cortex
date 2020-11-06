package com.hileco.cortex.symbolic.explore

import com.hileco.cortex.collections.BranchedStack


class StrongReference<T>(val referent: T?) {
    override fun equals(other: Any?): Boolean {
        return other is StrongReference<*> && referent === other.referent
    }

    override fun hashCode(): Int {
        return System.identityHashCode(referent)
    }

    override fun toString(): String {
        if (referent is BranchedStack<*>) {
            return referent.size().toString()
        }
        return "StrongReference(referent=$referent)"
    }
}
