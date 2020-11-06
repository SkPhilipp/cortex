package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import org.junit.Test

abstract class ProcessorFuzzTest {
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

        // TODO: Implement processor fuzzing
    }

    abstract fun fuzzTestableProcessor(): Processor

    @Test
    fun fuzzTest() {
        fuzzTestProcessor(fuzzTestableProcessor())
    }

    companion object {
        private const val LIMIT_RUNS = 100
    }
}
