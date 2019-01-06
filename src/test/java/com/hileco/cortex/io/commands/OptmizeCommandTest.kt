package com.hileco.cortex.io.commands

import org.junit.Assert
import org.junit.Test

class OptmizeCommandTest {
    @Test
    fun test() {
        val command = OptmizeCommand()
        val instructionStream = RunCommandTest::class.java.getResource("/assembly/barrier-01-immediate.cxasm").openStream()
        val optimized = command.execute(instructionStream)
        Assert.assertTrue(optimized.isNotEmpty())
    }
}