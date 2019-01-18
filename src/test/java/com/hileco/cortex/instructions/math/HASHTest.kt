package com.hileco.cortex.instructions.math

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class HASHTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(123)),
                HASH("SHA-512"))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/hash")
                .headingParagraph("HASH").paragraph("The HASH operation removes one element from the stack, performs the desired hashing " + "method on it and adds the resulting hash to the stack")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size().toLong(), 1)
    }
}
