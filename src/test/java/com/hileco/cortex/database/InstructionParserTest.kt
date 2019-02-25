package com.hileco.cortex.database

import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.debug.NOOP
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

    @Test
    fun testNoop() {
        val instructionParser = InstructionParser()
        val parsed = instructionParser.parse("")
        assertEquals(parsed, NOOP())
    }
}