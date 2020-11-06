package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.collections.backed.BackedInteger.Companion.ONE_32
import com.hileco.cortex.symbolic.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

@Ignore
class InliningProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        // a: <program calling fixed address>, <program at fixed address with call_return> ==> <program which has inlined instructions of the other one>
        val graphBuilder = GraphBuilder(listOf(
                InliningProcessor()
        ))
        val original = listOf(
                PUSH(ONE_32)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Assert.assertEquals(instructions, listOf(
                PUSH(ONE_32)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return InliningProcessor()
    }
}