package com.hileco.cortex.collections

import java.util.concurrent.atomic.AtomicLong

/**
 * A [BranchedComposite] is a structure which has multiple [Branched] members, each with their own tree structure.
 *
 * These structures' trees are not meant to be navigated through.
 */
abstract class BranchedComposite<T : BranchedComposite<T>> : Branched<T> {
    val id by lazy { ATOMIC_ID.incrementAndGet() }

    override fun parent(): T? {
        throw UnsupportedOperationException()
    }

    override fun id(): Long {
        return id
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
