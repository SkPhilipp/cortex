package com.hileco.cortex.io.commands

class RunCommandTest {
    fun test() {
        val command = RunCommand()
        val instructionStream = RunCommandTest::class.java.getResource("/assembly/winner-immediate.cxasm").openStream()
        command.execute(instructionStream)
    }
}