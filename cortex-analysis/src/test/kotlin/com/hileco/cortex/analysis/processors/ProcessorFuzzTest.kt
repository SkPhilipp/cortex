package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.fuzzing.ProgramGenerator
import com.hileco.cortex.vm.*
import com.hileco.cortex.vm.bytes.BackedInteger
import org.junit.Assert
import org.junit.Test

abstract class ProcessorFuzzTest {
    private fun executeAll(atlas: LayeredVmMap<BackedInteger, Program>): ProgramContext {
        val caller = Program(listOf())
        val callerContext = ProgramContext(caller)
        for (programAddress in atlas.keySet()) {
            val program = atlas[programAddress]
            val programContext = ProgramContext(program!!)
            val virtualMachine = VirtualMachine(callerContext, programContext)
            try {
                val programRunner = ProgramRunner(virtualMachine)
                programRunner.run()
            } catch (ignored: ProgramException) {
            }

        }
        return callerContext
    }

    /**
     * Fuzzes the processor and verifies correctness by executing non-processed and processed versions
     * of generated programs, comparing for any differences in call results, storage, or transfers.
     */
    private fun fuzzTestProcessor(processor: Processor) {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor(),
                processor
        ))

        val seed = System.currentTimeMillis() * LIMIT_RUNS
        var runs: Long = 0
        while (runs++ < LIMIT_RUNS) {
            val programGenerator = ProgramGenerator()
            val runSeed = seed + runs
            val generated = programGenerator.generate(runSeed)
            val generatedOptimized = programGenerator.generate(runSeed)
            for (address in generatedOptimized.keySet()) {
                val program = generatedOptimized[address]
                val instructions = program!!.instructions
                val optimizedInstructions = graphBuilder.build(instructions).toInstructions()
                generatedOptimized[program.address] = Program(optimizedInstructions, program.address)
            }
            val callerContext = executeAll(generated)
            val callerContextOptimized = executeAll(generatedOptimized)
            Assert.assertEquals("Issue with runSeed $runSeed in caller", callerContext.memory, callerContextOptimized.memory)
            Assert.assertEquals("Issue with runSeed $runSeed in caller", callerContext.program.storage, callerContextOptimized.program.storage)
            Assert.assertEquals("Issue with runSeed $runSeed in caller", callerContext.program.transfers, callerContextOptimized.program.transfers)
            for (address in generated.keySet()) {
                val standard = generated[address]
                val optimized = generatedOptimized[address]
                Assert.assertEquals("Issue with runSeed $runSeed in program $address", standard!!.storage, optimized!!.storage)
                Assert.assertEquals("Issue with runSeed $runSeed in program $address", standard.transfers, optimized.transfers)
            }
        }
    }

    abstract fun fuzzTestableProcessor(): Processor

    @Test
    fun fuzzTest() {
        fuzzTestProcessor(fuzzTestableProcessor())
    }

    companion object {
        private const val LIMIT_RUNS = 500
    }
}
