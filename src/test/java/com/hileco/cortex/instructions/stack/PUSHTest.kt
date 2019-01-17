package com.hileco.cortex.instructions.stack

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.ProgramException
import org.junit.Assert
import org.junit.Test

class PUSHTest : InstructionTest() {
    @Test
    @Throws(ProgramException::class)
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(1)),
                PUSH(byteArrayOf(10)),
                PUSH(byteArrayOf(100)))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/push")
                .headingParagraph("PUSH").paragraph("The PUSH operation adds one element to top of the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size().toLong(), 3)
        Assert.assertArrayEquals(stack.pop(), instructions[2].bytes)
        Assert.assertArrayEquals(stack.pop(), instructions[1].bytes)
        Assert.assertArrayEquals(stack.pop(), instructions[0].bytes)
    }
}
