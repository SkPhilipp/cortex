package com.hileco.cortex.instructions.debug

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.ProgramException
import org.junit.Test

class NOOPTest : InstructionTest() {
    @Test
    @Throws(ProgramException::class)
    fun run() {
        val instructions = listOf(NOOP())
        this.run(instructions)
        Documentation.of("instructions/noop")
                .headingParagraph("NOOP").paragraph("This operation does nothing. It is generally only used within optimization processes to replace " +
                        "instructions instead of having to removeAll them. This allows all JUMP-related instructions remain " +
                        "functional.")
                .paragraph("Example program:").source(instructions)
    }
}
