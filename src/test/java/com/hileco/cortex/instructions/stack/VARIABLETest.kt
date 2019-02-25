package com.hileco.cortex.instructions.stack

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.ExecutionVariable.ADDRESS
import com.hileco.cortex.instructions.stack.ExecutionVariable.INSTRUCTION_POSITION
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class VARIABLETest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                VARIABLE(ADDRESS),
                VARIABLE(INSTRUCTION_POSITION))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/variable")
                .headingParagraph("VARIABLE").paragraph("The VARIABLE adds the value of the referenced execution-bound variable to the stack.")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size(), 2)
        Assert.assertEquals(BigInteger(stack.pop()), 1.toBigInteger())
        Assert.assertEquals(BigInteger(stack.pop()), 0.toBigInteger())
    }
}
