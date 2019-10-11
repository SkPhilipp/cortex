package com.hileco.cortex.collections

import com.hileco.cortex.collections.backed.BackedVmMap
import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.collections.test.Variation
import org.junit.Assert
import org.junit.Test

class VmMapTest {
    @Test
    fun testBacked() {
        test { BackedVmMap() }
    }

    @Test
    fun testLayered() {
        test { LayeredVmMap() }
    }

    private fun test(mapSupplier: () -> VmMap<Int, String>) {
        Variation.fuzzed(100) { variation ->
            val vmStack = mapSupplier()
            variation.maybe { vmStack.copy() }
            vmStack[1] = "1"
            variation.maybe { vmStack.copy() }
            vmStack[2] = "2"
            variation.maybe { vmStack.copy() }
            vmStack[3] = "3"
            variation.maybe { vmStack.copy() }
            Assert.assertEquals("1", vmStack[1])
            Assert.assertEquals("2", vmStack[2])
            Assert.assertEquals("3", vmStack[3])
            variation.maybe { vmStack.copy() }
            vmStack.remove(1)
            variation.maybe { vmStack.copy() }
            vmStack.remove(2)
            variation.maybe { vmStack.copy() }
            vmStack.remove(3)
            variation.maybe { vmStack.copy() }
            Assert.assertNull(vmStack[1])
            Assert.assertNull(vmStack[2])
            Assert.assertNull(vmStack[3])
        }
    }
}