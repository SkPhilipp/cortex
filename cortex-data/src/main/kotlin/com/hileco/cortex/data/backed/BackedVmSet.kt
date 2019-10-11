package com.hileco.cortex.data.backed


import com.hileco.cortex.data.VmSet
import java.util.*

class BackedVmSet<T> : VmSet<T, BackedVmSet<T>> {
    private var backingSet: MutableSet<T>

    constructor() {
        this.backingSet = HashSet()
    }

    private constructor(backingSet: Set<T>) {
        this.backingSet = HashSet(backingSet)
    }

    override fun close() {}

    override fun copy(): BackedVmSet<T> {
        return BackedVmSet(backingSet)
    }

    override fun contains(value: T): Boolean {
        return backingSet.contains(value)
    }

    override fun add(value: T) {
        backingSet.add(value)
    }

    override fun remove(value: T) {
        backingSet.remove(value)
    }

    override fun size(): Int {
        return backingSet.size
    }
}
