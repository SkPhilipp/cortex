package com.hileco.cortex.io.commands

import com.hileco.cortex.analysis.attack.BarrierTest.Companion.BARRIER_02_ADDRESS
import com.hileco.cortex.database.Database
import com.hileco.cortex.io.commands.AttackCommand.Companion.METHOD_WINNER
import org.junit.Assert.assertTrue
import org.junit.Test

class AttackCommandTest {
    @Test
    fun test() {
        val command = AttackCommand()
        val program = Database.programRepository.findOne(BARRIER_02_ADDRESS)!!
        val solution = command.execute(METHOD_WINNER, program).first()
        assertTrue(solution.isSolvable)
    }
}