package com.hileco.cortex.instructions.stack

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import org.junit.Assert
import org.junit.Test

class DUPLICATETest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(100)),
                DUPLICATE(0))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/duplicate")
                .headingParagraph("DUPLICATE").paragraph("The DUPLICATE operation adds a duplicate of an element on the stack, to the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size(), 2)
        Assert.assertArrayEquals(stack.pop(), (instructions[0] as PUSH).bytes)
        Assert.assertArrayEquals(stack.pop(), (instructions[0] as PUSH).bytes)
    }
}
