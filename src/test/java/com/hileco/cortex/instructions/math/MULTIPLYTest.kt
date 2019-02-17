package com.hileco.cortex.instructions.math

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.VirtualMachine
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class MULTIPLYTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(10)),
                PUSH(byteArrayOf(10)),
                MULTIPLY())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/multiply")
                .headingParagraph("MULTIPLY").paragraph("The MULTIPLY operation removes two elements from the stack, multiplies them and puts" +
                        " the result on the stack. (This result may overflow if it would have been larger than ${VirtualMachine.NUMERICAL_LIMIT})")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(100))
    }

    @Test
    fun runOverflow() {
        val instructions = listOf(
                PUSH(VirtualMachine.NUMERICAL_LIMIT.toByteArray()),
                PUSH(10),
                MULTIPLY())
        val stack = this.run(instructions).stack
        val expected = VirtualMachine.NUMERICAL_LIMIT.multiply(BigInteger.TEN).mod(VirtualMachine.NUMERICAL_LIMIT.add(BigInteger.ONE))
        Assert.assertArrayEquals(expected.toByteArray(), stack.pop())
    }
}
