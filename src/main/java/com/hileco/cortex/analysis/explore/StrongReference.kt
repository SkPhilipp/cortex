package com.hileco.cortex.analysis.explore

import java.util.*

class StrongReference<T>(val referent: T?) {
    override fun equals(other: Any?): Boolean {
        return other is StrongReference<*> && referent === other.referent
    }

    override fun hashCode(): Int {
        return Objects.hash(referent)
    }
}