package com.hileco.cortex.collections.layer

import com.hileco.cortex.collections.VmStack
import com.hileco.cortex.collections.VmStackTest

class LayeredVmStackTest : VmStackTest() {
    override fun <V> implementation(): VmStack<V> {
        return LayeredVmStack()
    }
}