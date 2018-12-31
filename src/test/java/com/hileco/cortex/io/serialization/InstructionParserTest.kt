package com.hileco.cortex.io.serialization

import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.debug.HALT
import org.junit.Assert.assertEquals
import org.junit.Test

class InstructionParserTest {
    @Test
    fun test() {
        val instructionParser = InstructionParser()
        val parsed = instructionParser.parse("HALT WINNER")
        assertEquals(parsed, HALT(ProgramException.Reason.WINNER))
    }

    @Test
    fun testComment() {
        val instructionParser = InstructionParser()
        val parsed = instructionParser.parse("HALT WINNER -- you win")
        assertEquals(parsed, HALT(ProgramException.Reason.WINNER))
    }
}