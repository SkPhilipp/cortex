package com.hileco.cortex.fuzzer

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import org.junit.Assert
import org.junit.Test

class ProgramGeneratorFuzzTest {

    @Test
    fun fuzzTestGenerator() {
        val seed = System.currentTimeMillis() * LIMIT_RUNS
        var exceptions: Long = 0
        var runs: Long = 0
        while (runs < LIMIT_RUNS) {
            val programGenerator = ProgramGenerator()
            val generated = programGenerator.generate(seed + runs)
            val programAddresses = generated.keySet()
            for (programAddress in programAddresses) {
                runs++
                try {
                    val caller = Program(listOf())
                    val program = generated[programAddress]!!
                    val callerContext = ProgramContext(caller)
                    val programContext = ProgramContext(program)
                    val processContext = VirtualMachine(callerContext, programContext)
                    val programRunner = ProgramRunner(processContext)
                    programRunner.run()
                } catch (e: ProgramException) {
                    exceptions++
                }

            }
        }
        val ratio = exceptions.toDouble() / runs.toDouble()
        Assert.assertFalse(String.format("Too many ProgramExceptions per generated program: %f (seed: %d)", ratio, seed), ratio > 0.5)
    }

    @Test
    fun testFuzzerSample() {
        val programGenerator = ProgramGenerator()
        val generated = programGenerator.generate(0)
        val first = generated.keySet().iterator().next()
        val program = generated.get(first)
        Documentation.of("fuzzer/sample").source(program!!.instructions)
        Assert.assertTrue(!program.instructions.isEmpty())
    }

    companion object {

        private val LIMIT_RUNS = 100000
    }
}
