package com.hileco.cortex.collections

import com.hileco.cortex.collections.test.Variation
import org.junit.Assert
import org.junit.Test

abstract class VmSetTest {

    abstract fun <T> implementation(): VmSet<T>

    @Test
    fun fuzz() {
        Variation.fuzzed(100) { variation ->
            val layeredSet = implementation<Int>()
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