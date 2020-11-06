package com.hileco.cortex.collections

import org.junit.Assert.assertEquals
import org.junit.Test

class BranchedStackTest {

    @Test
    fun fuzz() {
        Variation.fuzzed(100) { variation ->
            val vmStack = BranchedStack<Int>()
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