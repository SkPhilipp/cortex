package com.hileco.cortex.collections

import com.hileco.cortex.collections.backed.BackedVmStack
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.collections.test.Variation
import org.junit.Assert.assertEquals
import org.junit.Test

class VmStackTest {
    @Test
    fun testBacked() {
        test { BackedVmStack() }
    }

    @Test
    fun testLayered() {
        test { LayeredVmStack() }
    }

    private fun test(stackSupplier: () -> VmStack<Int>) {
        Variation.fuzzed(100) { variation ->
            val vmStack = stackSupplier()
            variation.maybe { vmStack.copy() }
            vmStack.push(1)
            variation.maybe { vmStack.copy() }
            vmStack.push(2)
            variation.maybe { vmStack.copy() }
            vmStack.push(3)
            variation.maybe { vmStack.copy() }
            assertEquals(Integer.valueOf(3), vmStack.peek())
            assertEquals(Integer.valueOf(3), vmStack.pop())
            variation.maybe { vmStack.copy() }
            assertEquals(Integer.valueOf(2), vmStack.peek())
            assertEquals(Integer.valueOf(2), vmStack.pop())
            variation.maybe { vmStack.copy() }
            assertEquals(Integer.valueOf(1), vmStack.peek())
            assertEquals(Integer.valueOf(1), vmStack.pop())
        }
    }
}