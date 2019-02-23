package com.hileco.cortex.fuzzer

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.vm.concrete.Program
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
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
                    val virtualMachine = VirtualMachine(callerContext, programContext)
                    val programRunner = ProgramRunner(virtualMachine)
                    programRunner.run()
                } catch (e: ProgramException) {
                    exceptions++
                }

            }
        }
        val ratio = exceptions.toDouble() / runs.toDouble()
        Assert.assertFalse("Too many ProgramExceptions per generated program: $ratio (seed: $seed)", ratio > 0.5)
    }

    @Test
    fun testFuzzerSample() {
        val programGenerator = ProgramGenerator()
        val generated = programGenerator.generate(0)
        val first = generated.keySet().first()
        val program = generated[first]
        Documentation.of("fuzzer/sample").source(program!!.instructions)
        Assert.assertTrue(!program.instructions.isEmpty())
    }

    companion object {
        private const val LIMIT_RUNS = 1000
    }
}
