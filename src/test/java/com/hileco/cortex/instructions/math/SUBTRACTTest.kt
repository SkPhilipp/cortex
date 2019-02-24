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
    fun symbolicSubtractValueToValue() {
        val result = runSymbolic(SUBTRACT(), Value(1), Value(1))
        Assert.assertEquals(Value(0), result)
    }

    @Test
    fun symbolicSubtractValueToSubtractVariableToValue() {
        val result = runSymbolic(SUBTRACT(), Value(1), Subtract(Stack(0), Value(1)))
        Assert.assertEquals(Subtract(Stack(0), Value(0)), result)
    }

    @Test
    fun symbolicSubtractValueToSubtractValuetoVariable() {
        val result = runSymbolic(SUBTRACT(), Value(1), Subtract(Value(1), Stack(0)))
        Assert.assertEquals(Subtract(Stack(0), Value(0)), result)
    }
}
