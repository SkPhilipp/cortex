package com.hileco.cortex.instructions.math

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.VirtualMachine
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

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
                        "result on the stack. (This result may overflow if it would have been larger than ${VirtualMachine.NUMERICAL_LIMIT})")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(101))
    }

    @Test
    fun runOverflow() {
        val instructions = listOf(
                PUSH(VirtualMachine.NUMERICAL_LIMIT.toByteArray()),
                PUSH(10),
                ADD())
        val stack = this.run(instructions).stack
        Assert.assertArrayEquals(BigInteger.valueOf(9).toByteArray(), stack.pop())
    }
}
