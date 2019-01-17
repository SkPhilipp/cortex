package com.hileco.cortex.instructions.jumps

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class JUMPTest : InstructionTest() {
    @Test
    @Throws(ProgramException::class)
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(3)),
                JUMP(),
                PUSH(byteArrayOf(100)),
                JUMP_DESTINATION())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/jump")
                .headingParagraph("JUMP").paragraph("The JUMP operation removes one element from the stack, using it to set the instruction position of the " +
                        "program itself. JUMPs may only result in instruction positions which point to a JUMP_DESTINATION instruction. The JUMP_DESTINATION by itself is equal to a NOOP.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size().toLong(), 0)
    }
}
