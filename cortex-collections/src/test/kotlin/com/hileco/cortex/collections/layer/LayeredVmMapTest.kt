package com.hileco.cortex.collections.layer

import com.hileco.cortex.collections.VmMap
import com.hileco.cortex.collections.VmMapTest

class LayeredVmMapTest : VmMapTest() {
    override fun <K, V> implementation(): VmMap<K, V> {
        return LayeredVmMap()
    }
}