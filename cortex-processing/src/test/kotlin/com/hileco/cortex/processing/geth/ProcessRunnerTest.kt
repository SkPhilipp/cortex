package com.hileco.cortex.processing.geth

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test

class ProcessRunnerTest {
    @Test
    fun testSuccessfulInvocation() {
        val processRunner = ProcessRunner()
        val processResult = processRunner.execute(listOf("git", "--version"))
        println(processResult)
        assertEquals(0, processResult.exitCode)
        assertTrue(processResult.output.isNotEmpty())
        assertTrue(processResult.errors.isEmpty())
    }

    @Test
    fun testErrorInvocation() {
        val processRunner = ProcessRunner()
        val processResult = processRunner.execute(listOf("git", "error-invocation"))
        println(processResult)
        assertEquals(1, processResult.exitCode)
        assertTrue(processResult.output.isEmpty())
        assertTrue(processResult.errors.isNotEmpty())
    }
}