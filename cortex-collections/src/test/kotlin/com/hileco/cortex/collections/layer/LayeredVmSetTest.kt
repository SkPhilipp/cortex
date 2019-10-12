package com.hileco.cortex.collections.layer

import com.hileco.cortex.collections.VmSet
import com.hileco.cortex.collections.VmSetTest

class LayeredVmSetTest : VmSetTest() {
    override fun <V> implementation(): VmSet<V> {
        return LayeredVmSet()
    }
}