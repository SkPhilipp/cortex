package com.hileco.cortex.io.commands

import com.hileco.cortex.analysis.attack.BarrierTest.Companion.BARRIER_01_ADDRESS
import com.hileco.cortex.database.Database
import org.junit.Assert
import org.junit.Test

class OptmizeCommandTest {
    @Test
    fun test() {
        val command = OptmizeCommand()
        val program = Database.programRepository.findOne(BARRIER_01_ADDRESS)!!
        val optimized = command.execute(program)
        Assert.assertTrue(optimized.isNotEmpty())
    }
}