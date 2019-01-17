package com.hileco.cortex.instructions.jumps

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class JUMP_IFTest : InstructionTest() {
    @Test
    @Throws(ProgramException::class)
    fun run() {
        val instructions = listOf(PUSH(byteArrayOf(1)),
                PUSH(byteArrayOf(4)),
                JUMP_IF(),
                PUSH(byteArrayOf(100)),
                JUMP_DESTINATION())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/jump-if")
                .headingParagraph("JUMP_IF").paragraph("The JUMP_IF operation removes two elements from the stack, using the top element to set the " +
                        "instruction position of the program itself, depending on whether the second element is a " +
                        "positive value. JUMP_IFs may only result in instruction positions which point to a " +
                        "JUMP_DESTINATION instruction. The JUMP_DESTINATION by itself is equal to a NOOP.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size().toLong(), 0)
    }
}
