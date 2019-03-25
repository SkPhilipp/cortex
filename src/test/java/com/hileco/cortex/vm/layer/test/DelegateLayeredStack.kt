package com.hileco.cortex.vm.layer.test

import com.hileco.cortex.vm.layer.DelegateLayered
import com.hileco.cortex.vm.layer.LayeredStack

class DelegateLayeredStack<T>(val delegate: LayeredStack<T> = LayeredStack()) : DelegateLayered<DelegateLayeredStack<T>>() {
    override fun recreateParent(): DelegateLayeredStack<T> {
        return DelegateLayeredStack(delegate.parent() ?: LayeredStack())
    }

    override fun branchDelegates(): DelegateLayeredStack<T> {
        val branchedDelegate = delegate.branch()
        return DelegateLayeredStack(branchedDelegate)
    }

    override fun disposeDelegates() {
        delegate.dispose()
    }
}
