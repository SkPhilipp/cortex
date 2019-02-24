package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression.*
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class SUBTRACTTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(1)),
                PUSH(byteArrayOf(100)),
                SUBTRACT())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/subtract")
                .headingParagraph("SUBTRACT").paragraph("The SUBTRACT operation removes two elements from the stack, subtracts the second element from the " + "top element and puts the result on the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(99))
    }

    @Test
    fun symbolicValueToValue() {
        val result = runSymbolic(SUBTRACT(), Value(1), Value(1))
        Assert.assertEquals(Value(0), result)
    }

    /**
     * Tests for "x - 0 == x"
     */
    @Test
    fun symbolicZero() {
        val result = runSymbolic(SUBTRACT(), Stack(0), Value(0))
        Assert.assertEquals(Stack(0), result)
    }

    /**
     * Tests for "2 - ( x - 1 ) == 3 - x"
     */
    @Test
    fun symbolicValueToSubtractVariableToValue() {
        val result = runSymbolic(SUBTRACT(), Value(2), Subtract(Stack(0), Value(1)))
        Assert.assertEquals(Subtract(Value(3), Stack(0)), result)
    }

    /**
     * Tests for "2 - ( 1 - x ) == x + 1" (or rather "x - -1")
     */
    @Test
    fun symbolicValueToSubtractValuetoVariable() {
        val result = runSymbolic(SUBTRACT(), Value(2), Subtract(Value(1), Stack(0)))
        Assert.assertEquals(Subtract(Stack(0), Value(-1)), result)
    }

    /**
     * Tests for "( x - 2 ) - 1 == x - 3"
     */
    @Test
    fun symbolicSubtractVariableToValueToValue() {
        val result = runSymbolic(SUBTRACT(), Subtract(Stack(0), Value(2)), Value(1))
        Assert.assertEquals(Subtract(Stack(0), Value(3)), result)
    }

    /**
     * Tests for "( 2 - x ) - 1 == 1 - x"
     */
    @Test
    fun symbolicSubtractValuetoVariableToValue() {
        val result = runSymbolic(SUBTRACT(), Subtract(Value(2), Stack(0)), Value(1))
        Assert.assertEquals(Subtract(Value(1), Stack(0)), result)
    }
}
