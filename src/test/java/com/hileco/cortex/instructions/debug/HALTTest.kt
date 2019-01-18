package com.hileco.cortex.instructions.debug

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.ProgramException
import org.junit.Test

class HALTTest : InstructionTest() {
    @Test(expected = ProgramException::class)
    fun run() {
        val instructions = listOf(
                HALT(ProgramException.Reason.STACK_LIMIT_REACHED))
        Documentation.of("instructions/halt")
                .headingParagraph("HALT").paragraph("The HALT operation cancels execution of the entire process, and provides a reason for doing so. It is " +
                        "generally only used within optimization processed to replace instructions which would otherwise " +
                        "cause the same errors during runtime.")
                .paragraph("Example program:").source(instructions)
        this.run(instructions)
    }
}