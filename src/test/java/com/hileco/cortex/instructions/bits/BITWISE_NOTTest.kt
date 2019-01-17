package com.hileco.cortex.instructions.bits

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class BITWISE_NOTTest : InstructionTest() {
    @Test
    @Throws(ProgramException::class)
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(127)),
                BITWISE_NOT())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/bitwise-not")
                .headingParagraph("BITWISE_NOT").paragraph("The BITWISE_NOT operation performs logical negation on each bit of the top element on the stack")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size().toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(-128))
    }
}
