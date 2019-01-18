package com.hileco.cortex.instructions.conditions

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class LESS_THANTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(100)),
                PUSH(byteArrayOf(10)),
                LESS_THAN())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/less-than")
                .headingParagraph("LESS_THAN").paragraph("The LESS_THAN operation removes two elements from the stack, then adds a 1 or 0 to the stack" + " depending on whether the top element was less than the second element.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size().toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), ConditionInstruction.TRUE)
    }
}
