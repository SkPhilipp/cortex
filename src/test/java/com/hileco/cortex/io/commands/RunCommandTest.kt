package com.hileco.cortex.io.commands

import com.hileco.cortex.analysis.attack.BarrierTest.Companion.BARRIER_01_ADDRESS
import com.hileco.cortex.database.Database
import com.hileco.cortex.vm.layer.LayeredBytes

class RunCommandTest {
    fun test() {
        val command = RunCommand()
        val program = Database.programRepository.findOne(BARRIER_01_ADDRESS)!!
        command.execute(program, LayeredBytes())
    }
}