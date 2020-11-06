package com.hileco.cortex.collections

import java.io.Closeable

/**
 * [Branched] instances are objects whose [copy] has no relation to its space requirements.
 *   Such copies come at a cost of a slight time impact on future operations on the object and its copy and their future copies.
 *   Meaning; a 1kb [Branched] structure and a 100GB [Branched] structure take the same amount of time to copy.
 *   Copying has the same, negligible space cost for both instances.
 */
interface Branched<T : Branched<T>> : Closeable {

    /**
     * To be invoked when an instance is no longer needed, this may allow the [Branched] implementation to rearrange its internal structure and potentially
     * save space or time either immediately or on future operations.
     */
    override fun close()

    /**
     * An immediate [Branched] copy of type [T] using minimal space.
     */
    fun copy(): T
}
