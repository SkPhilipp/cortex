package com.hileco.cortex.instructions.stack

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import org.junit.Assert
import org.junit.Test

class POPTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(1)),
                PUSH(byteArrayOf(100)),
                POP())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/pop")
                .headingParagraph("POP").paragraph("The POP operation removes the top element from the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size(), 1)
        Assert.assertArrayEquals(stack.pop(), (instructions[0] as PUSH).bytes)
    }
}
