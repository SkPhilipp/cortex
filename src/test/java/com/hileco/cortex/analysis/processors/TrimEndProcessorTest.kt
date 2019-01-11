package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

import java.math.BigInteger

class TrimEndProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val graphBuilder = GraphBuilder(listOf(
                TrimEndProcessor()

        ))
        val original = listOf(
                EXIT(),
                PUSH(BigInteger.TEN.toByteArray()),
                PUSH(BigInteger.ONE.toByteArray()),
                JUMP_DESTINATION(),
                PUSH(BigInteger.TEN.toByteArray()),
                PUSH(BigInteger.ONE.toByteArray())
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(TrimEndProcessor::class.simpleName!!)
                .headingParagraph(TrimEndProcessor::class.simpleName!!)
                .paragraph("Removes any instructions within the same jump-reachable block following another instruction that guarantees the instructions will not be reached.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                EXIT(),
                NOOP(),
                NOOP(),
                JUMP_DESTINATION(),
                PUSH(BigInteger.TEN.toByteArray()),
                PUSH(BigInteger.ONE.toByteArray())
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return TrimEndProcessor()
    }
}