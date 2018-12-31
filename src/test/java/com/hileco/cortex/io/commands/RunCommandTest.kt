package com.hileco.cortex.io.commands

import com.hileco.cortex.vm.layer.LayeredBytes

class RunCommandTest {
    fun test() {
        val command = RunCommand()
        val instructionStream = RunCommandTest::class.java.getResource("/assembly/winner-immediate.cxasm").openStream()
        command.execute(instructionStream, LayeredBytes())
    }
}