package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression.*
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramConstants.Companion.OVERFLOW_LIMIT
import org.junit.Assert
import org.junit.Test

class ADDTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(100)),
                PUSH(byteArrayOf(1)),
                ADD())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/add")
                .headingParagraph("ADD").paragraph("The ADD operation removes two elements from the stack, adds them together and puts the " +
                        "result on the stack. (This result may overflow if it would have been larger than $OVERFLOW_LIMIT)")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(101))
    }

    @Test
    fun runOverflow() {
        val instructions = listOf(
                PUSH(OVERFLOW_LIMIT.toByteArray()),
                PUSH(10),
                ADD())
        val stack = this.run(instructions).stack
        Assert.assertArrayEquals(9.toBigInteger().toByteArray(), stack.pop())
    }

    @Test
    fun symbolicValueToValue() {
        val result = runSymbolic(ADD(), Value(1), Value(1))
        Assert.assertEquals(Value(2), result)
    }

    @Test
    fun symbolicValueToAddVariableToValue() {
        val result = runSymbolic(ADD(), Value(1), Add(Stack(0), Value(1)))
        Assert.assertEquals(Add(Stack(0), Value(2)), result)
    }

    @Test
    fun symbolicValueToAddValuetoVariable() {
        val result = runSymbolic(ADD(), Value(1), Add(Value(1), Stack(0)))
        Assert.assertEquals(Add(Stack(0), Value(2)), result)
    }

}
