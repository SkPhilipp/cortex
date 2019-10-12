package com.hileco.cortex.collections.backed

import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.VmStackTest

class BackedVmStackTest : VmStackTest() {
    override fun <V> implementation(): VmStack<V> {
        return BackedVmStack()
    }
}