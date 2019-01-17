package com.hileco.cortex.instructions.stack

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.ProgramException
import org.junit.Assert
import org.junit.Test

class SWAPTest : InstructionTest() {
    @Test
    @Throws(ProgramException::class)
    fun run() {
        val instructions = listOf(
                PUSH(byteArrayOf(100)),
                PUSH(byteArrayOf(1)),
                SWAP(0, 1))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/swap")
                .headingParagraph("SWAP").paragraph("The SWAP operation swaps two elements on the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size().toLong(), 2)
        Assert.assertArrayEquals(stack.pop(), (instructions[0] as PUSH).bytes)
        Assert.assertArrayEquals(stack.pop(), (instructions[1] as PUSH).bytes)
    }
}
