package com.hileco.cortex.collections

import com.hileco.cortex.collections.backed.BackedVmSet
import com.hileco.cortex.collections.layer.LayeredVmSet
import com.hileco.cortex.collections.test.Variation
import org.junit.Assert
import org.junit.Test

class VmSetTest {
    @Test
    fun testBacked() {
        test { BackedVmSet() }
    }

    @Test
    fun testLayered() {
        test { LayeredVmSet() }
    }

    private fun test(setSupplier: () -> VmSet<Int>) {
        Variation.fuzzed(100) { variation ->
            val layeredSet = setSupplier()
            variation.maybe { layeredSet.copy() }
            layeredSet.add(1)
            variation.maybe { layeredSet.copy() }
            layeredSet.add(2)
            variation.maybe { layeredSet.copy() }
            layeredSet.add(3)
            variation.maybe { layeredSet.copy() }
            Assert.assertTrue(layeredSet.contains(1))
            Assert.assertTrue(layeredSet.contains(2))
            Assert.assertTrue(layeredSet.contains(3))
            variation.maybe { layeredSet.copy() }
            layeredSet.remove(1)
            variation.maybe { layeredSet.copy() }
            layeredSet.remove(2)
            variation.maybe { layeredSet.copy() }
            layeredSet.remove(3)
            variation.maybe { layeredSet.copy() }
            Assert.assertFalse(layeredSet.contains(1))
            Assert.assertFalse(layeredSet.contains(2))
            Assert.assertFalse(layeredSet.contains(3))
        }
    }
}