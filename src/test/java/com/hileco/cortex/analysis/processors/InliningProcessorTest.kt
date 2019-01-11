package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.math.BigInteger

class InliningProcessorTest : ProcessorFuzzTest() {
    @Ignore
    @Test
    fun process() {
        // a: <program calling fixed address>, <program at fixed address with call_return> ==> <program which has inlined instructions of the other one>
        val graphBuilder = GraphBuilder(listOf(
                InliningProcessor()
        ))
        val original = listOf(
                PUSH(BigInteger.ONE.toByteArray())
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(InliningProcessor::class.java.simpleName)
                .headingParagraph(InliningProcessor::class.java.simpleName)
                .paragraph("Moved instructions from a target program into a source program.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                PUSH(BigInteger.ONE.toByteArray())
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return InliningProcessor()
    }
}