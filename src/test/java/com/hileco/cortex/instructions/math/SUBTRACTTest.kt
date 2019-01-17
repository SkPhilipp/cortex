package com.hileco.cortex.instructions.math

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class SUBTRACTTest : InstructionTest() {
    @Test
    @Throws(ProgramException::class)
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(1)),
                PUSH(byteArrayOf(100)),
                SUBTRACT())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/subtract")
                .headingParagraph("SUBTRACT").paragraph("The SUBTRACT operation removes two elements from the stack, subtracts the second element from the " + "top element and puts the result on the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size().toLong(), 1)
        Assert.assertArrayEquals(stack.pop(), byteArrayOf(99))
    }
}
