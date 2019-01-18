package com.hileco.cortex.instructions.bits

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class BITWISE_XORTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(5)),
                PUSH(byteArrayOf(3)),
                BITWISE_XOR())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/bitwise-xor")
                .headingParagraph("BITWISE_XOR").paragraph("The BITWISE_XOR operation performs a bitwise XOR operation on each bit of the top two elements on" + " the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size().toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(6))
    }
}
