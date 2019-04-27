package com.hileco.cortex.instructions.debug

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class DROPTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(1)),
                PUSH(byteArrayOf(10)),
                PUSH(byteArrayOf(100)),
                DROP(2))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/drop")
                .headingParagraph("DROP").paragraph("The DROP operation removes multiple elements from the top of the stack." +
                        " It is generally only used within optimization or transpiler processes," +
                        " to replace NOOP-like instructions which remove elements from the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size(), 1)
        Assert.assertArrayEquals(stack.pop(), (instructions[0] as PUSH).bytes)
    }
}
