package com.hileco.cortex.instructions.jumps

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class JUMP_DESTINATIONTest : InstructionTest() {
    @Test
    @Throws(ProgramException::class)
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(3)),
                JUMP(),
                PUSH(byteArrayOf(100)),
                JUMP_DESTINATION())
        val stack = this.run(instructions).stack
        Documentation.of("instructions/jump-destination")
                .headingParagraph("JUMP_DESTINATION").paragraph("Marks a part of a program as being able to be jumped to. Often used in analysis to split code up into basic blocks.")
        Assert.assertEquals(stack.size().toLong(), 0)
    }
}
