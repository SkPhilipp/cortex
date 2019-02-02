package com.hileco.cortex.server.serialization

import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.server.serialization.InstructionParser
import org.junit.Assert.assertEquals
import org.junit.Test

class InstructionParserTest {
    @Test
    fun test() {
        val instructionParser = InstructionParser()
        val parsed = instructionParser.parse("HALT WINNER")
        assertEquals(parsed, HALT(WINNER))
    }

    @Test
    fun testComment() {
        val instructionParser = InstructionParser()
        val parsed = instructionParser.parse("HALT WINNER -- you win")
        assertEquals(parsed, HALT(WINNER))
    }
}