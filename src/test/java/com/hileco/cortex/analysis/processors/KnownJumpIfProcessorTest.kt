package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

import java.math.BigInteger

class KnownJumpIfProcessorTest : ProcessorFuzzTest() {

    @Test
    fun testProcess() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                KnownJumpIfProcessor()

        ))
        val original = listOf(
                PUSH(BigInteger.ONE.toByteArray()),
                PUSH(BigInteger.TEN.toByteArray()),
                JUMP_IF()
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(KnownJumpIfProcessor::class.simpleName!!)
                .headingParagraph(KnownJumpIfProcessor::class.simpleName!!)
                .paragraph("Replaces JUMP_IF instructions with JUMP or NOOP instructions where the condition is known ahead of time.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                NOOP(),
                PUSH(BigInteger.TEN.toByteArray()),
                JUMP()
        ))
    }

    internal override fun fuzzTestableProcessor(): Processor {
        return KnownJumpIfProcessor()
    }
}