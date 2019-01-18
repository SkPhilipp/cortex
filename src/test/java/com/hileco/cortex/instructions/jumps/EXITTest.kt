package com.hileco.cortex.instructions.jumps

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class EXITTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(10)),
                EXIT(),
                PUSH(byteArrayOf(100)))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/exit")
                .headingParagraph("EXIT").paragraph("The EXIT operation ends execution of the program.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size().toLong(), 1)
        Assert.assertNotEquals(stack.pop(), byteArrayOf(10))
    }
}
