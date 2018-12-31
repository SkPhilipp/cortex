package com.hileco.cortex.io.commands

import com.hileco.cortex.instructions.ProgramException
import org.junit.Test

class RunCommandTest {
    @Test(expected = ProgramException::class)
    fun test() {
        val command = RunCommand()
        val instructionStream = RunCommandTest::class.java.getResource("/assembly/winner-immediate.cxasm").openStream()
        command.execute(instructionStream)
    }
}