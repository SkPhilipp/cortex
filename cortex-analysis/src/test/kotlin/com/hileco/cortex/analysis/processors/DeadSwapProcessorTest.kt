package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.collections.backed.BackedInteger.Companion.ONE_32
import com.hileco.cortex.collections.backed.toBackedInteger
import com.hileco.cortex.symbolic.instructions.debug.NOOP
import com.hileco.cortex.symbolic.instructions.stack.PUSH
import com.hileco.cortex.symbolic.instructions.stack.SWAP
import org.junit.Assert
import org.junit.Test

class DeadSwapProcessorTest : ProcessorFuzzTest() {
    @Test
    fun testProcess() {
        val graphBuilder = GraphBuilder(listOf(
                DeadSwapProcessor()

        ))
        val original = listOf(
                PUSH(ONE_32),
                PUSH(10.toBackedInteger()),
                SWAP(0, 0)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Assert.assertEquals(instructions, listOf(
                PUSH(ONE_32),
                PUSH(10.toBackedInteger()),
                NOOP()
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return DeadSwapProcessor()
    }
}