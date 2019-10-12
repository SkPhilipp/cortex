package com.hileco.cortex.collections.backed

import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.collections.VmMapTest

class BackedVmMapTest : VmMapTest() {
    override fun <K, V> implementation(): VmMap<K, V> {
        return BackedVmMap()
    }
}