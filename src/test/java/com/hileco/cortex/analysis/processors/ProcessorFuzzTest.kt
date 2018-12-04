package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.fuzzer.ProgramGenerator
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import com.hileco.cortex.vm.layer.LayeredMap
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

abstract class ProcessorFuzzTest {

    private fun executeAll(atlas: LayeredMap<BigInteger, Program>): ProgramContext {
        val caller = Program(BigInteger.ZERO, listOf())
        val callerContext = ProgramContext(caller)
        for (programAddress in atlas.keySet()) {
            val program = atlas.get(programAddress)
            val programContext = ProgramContext(program)
            val processContext = VirtualMachine(callerContext, programContext)
            try {
                val programRunner = ProgramRunner(processContext)
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
                val program = generatedOptimized.get(address)
                val instructions = program!!.instructions
                val optimizedInstructions = graphBuilder.build(instructions).toInstructions()
                generatedOptimized.put(program.address, Program(program.address, optimizedInstructions))
            }
            val callerContext = this.executeAll(generated)
            val callerContextOptimized = this.executeAll(generatedOptimized)
            Assert.assertEquals(String.format("Issue with runSeed %d in caller", runSeed), callerContext.memory,
                    callerContextOptimized.memory)
            Assert.assertEquals(String.format("Issue with runSeed %d in caller", runSeed), callerContext.program.storage,
                    callerContextOptimized.program.storage)
            Assert.assertEquals(String.format("Issue with runSeed %d in caller", runSeed), callerContext.program.transfers,
                    callerContextOptimized.program.transfers)
            for (address in generated.keySet()) {
                val standard = generated.get(address)
                val optimized = generatedOptimized.get(address)
                Assert.assertEquals(String.format("Issue with runSeed %d in program %s", runSeed, address.toString()),
                        standard!!.storage,
                        optimized!!.storage)
                Assert.assertEquals(String.format("Issue with runSeed %d in program %s", runSeed, address.toString()),
                        standard.transfers,
                        optimized.transfers)
            }
        }
    }

    internal abstract fun fuzzTestableProcessor(): Processor

    @Test
    fun fuzzTest() {
        this.fuzzTestProcessor(this.fuzzTestableProcessor())
    }

    companion object {
        private const val LIMIT_RUNS = 500
    }
}
