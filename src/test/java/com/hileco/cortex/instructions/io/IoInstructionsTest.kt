package com.hileco.cortex.instructions.io

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.concrete.ProgramStoreZone
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class IoInstructionsTest : InstructionTest() {
    @Test
    fun run() {
        val instructions = listOf(
                PUSH(10),
                PUSH(1234),
                SAVE(ProgramStoreZone.MEMORY),
                PUSH(1234),
                LOAD(ProgramStoreZone.MEMORY)
        )
        val stack = this.run(instructions).stack
        Documentation.of("instructions/save-and-load")
                .headingParagraph("SAVE & LOAD").paragraph("The SAVE operation removes two elements from the stack, using the top element as an" +
                        " address and the second element as a value to write into the area specified" +
                        " (${ProgramStoreZone.MEMORY}, or ${ProgramStoreZone.DISK})." +
                        " The LOAD  operation removes one element from the stack, using it as an" +
                        " address to read from the area specified (${ProgramStoreZone.MEMORY}, ${ProgramStoreZone.DISK}, or ${ProgramStoreZone.CALL_DATA}).")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
        Assert.assertEquals(stack.size(), 1)
        Assert.assertEquals(BigInteger(stack.pop()), BigInteger.valueOf(10))
    }
}