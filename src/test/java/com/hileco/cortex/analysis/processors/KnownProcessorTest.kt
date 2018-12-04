package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.math.ADD
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

import java.math.BigInteger

class KnownProcessorTest : ProcessorFuzzTest() {

    @Test
    fun process() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                KnownProcessor()
        ))
        val original = listOf(
                PUSH(BigInteger.ONE.toByteArray()),
                PUSH(BigInteger.TEN.toByteArray()),
                ADD()
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()
        Documentation.of(KnownProcessor::class.simpleName!!)
                .headingParagraph(KnownProcessor::class.simpleName!!)
                .paragraph("Precomputes instructions which modify only the stack and do not have any external dependencies.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)
        Assert.assertEquals(instructions, listOf(
                NOOP(),
                NOOP(),
                PUSH(BigInteger.valueOf(11).toByteArray())
        ))
    }

    internal override fun fuzzTestableProcessor(): Processor {
        return KnownProcessor()
    }
}