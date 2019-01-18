package com.hileco.cortex.instructions.bits

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class BITWISE_ANDTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(5)),
                PUSH(byteArrayOf(3)),
                BITWISE_AND())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/bitwise-and")
                .headingParagraph("BITWISE_AND").paragraph("The BITWISE_AND operation performs a bitwise AND operation on each bit of the top two elements on" + " the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size().toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(1))
    }
}
