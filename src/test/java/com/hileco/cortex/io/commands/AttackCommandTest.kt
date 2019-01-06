package com.hileco.cortex.io.commands

import com.hileco.cortex.io.commands.AttackCommand.Companion.METHOD_WINNER
import org.junit.Assert.assertTrue
import org.junit.Test

class AttackCommandTest {
    @Test
    fun test() {
        val command = AttackCommand()
        val instructionStream = RunCommandTest::class.java.getResource("/assembly/barrier-02-basic.cxasm").openStream()
        val solution = command.execute(METHOD_WINNER, instructionStream).first()
        assertTrue(solution.isSolvable)
    }
}