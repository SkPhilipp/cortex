package com.hileco.cortex.collections.backed

import com.hileco.cortex.collections.VmSet
import com.hileco.cortex.collections.VmSetTest

class BackedVmSetTest : VmSetTest() {
    override fun <V> implementation(): VmSet<V> {
        return BackedVmSet()
    }
}