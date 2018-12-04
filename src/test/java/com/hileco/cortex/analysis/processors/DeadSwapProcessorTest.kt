package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.instructions.stack.SWAP
import org.junit.Assert
import org.junit.Test

import java.math.BigInteger

class DeadSwapProcessorTest : ProcessorFuzzTest() {

    @Test
    fun testProcess() {
        val graphBuilder = GraphBuilder(listOf(
                DeadSwapProcessor()

        ))
        val original = listOf(
                PUSH(BigInteger.ONE.toByteArray()),
                PUSH(BigInteger.TEN.toByteArray()),
                SWAP(0, 0)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(DeadSwapProcessor::class.simpleName!!)
                .headingParagraph(DeadSwapProcessor::class.simpleName!!)
                .paragraph("Removes SWAP instructions which swap an element on the stack with the same element.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                PUSH(BigInteger.ONE.toByteArray()),
                PUSH(BigInteger.TEN.toByteArray()),
                NOOP()
        ))
    }

    internal override fun fuzzTestableProcessor(): Processor {
        return DeadSwapProcessor()
    }
}