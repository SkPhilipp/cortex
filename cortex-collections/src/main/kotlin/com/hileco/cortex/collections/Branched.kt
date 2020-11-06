package com.hileco.cortex.collections

import java.io.Closeable

/**
 * [Branched] represents a branch in a tree structure, where every branch inherits all data from its parent branch.
 */
interface Branched<T : Branched<T>> : Closeable {

    /**
     * To be invoked when an instance is no longer needed, this may allow the [Branched] implementation to rearrange its internal structure and potentially
     * save space or time either immediately or on future operations.
     */
    override fun close()

    /**
     * A copy of this [Branched] containing the same data contained in this [Branched]. [copy]'s performance is not related to the amount of data currently
     * in the entire tree structure. [copy] however comes at a cost of performance degradation as the tree depth likely increases with every invocation.
     * Meaning; [copy] takes the same amount of time and has the same performance impact on a 100GB tree as it would on a 1KB tree.
     */
    fun copy(): T

    /**
     * A view of the current parent [Branched] of this object, the parent [Branched] is dynamic and may change over time.
     * Returns `null` when there is no parent [Branched], indicating this is a root node in the tree structure.
     *
     * Objects returned by this method should be used read-only as it may affect all child [Branched].
     */
    fun parent(): T?

    /**
     * Identifies a unique [Branched].
     */
    fun id(): Long
}
