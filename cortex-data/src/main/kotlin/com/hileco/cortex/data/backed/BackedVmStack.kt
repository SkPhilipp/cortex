package com.hileco.cortex.data.backed

import com.hileco.cortex.data.base.BaseVmStack
import java.util.*

class BackedVmStack<T> : BaseVmStack<T, BackedVmStack<T>> {
    private var backingList: MutableList<T>

    constructor() {
        this.backingList = ArrayList()
    }

    private constructor(backingList: List<T>) {
        this.backingList = ArrayList(backingList)
    }

    override fun get(index: Int): T {
        return backingList[index]
    }

    override fun set(index: Int, value: T) {
        backingList[index] = value
    }

    override fun clear() {
        backingList.clear()
    }

    override fun close() {}

    override fun copy(): BackedVmStack<T> {
        return BackedVmStack(backingList)
    }

    override fun peek(offset: Int): T {
        return backingList[backingList.size - offset - 1]
    }

    override fun push(value: T) {
        backingList.add(value)
    }

    override fun pop(): T {
        return backingList.removeAt(backingList.size - 1)
    }

    override fun size(): Int {
        return backingList.size
    }
}
