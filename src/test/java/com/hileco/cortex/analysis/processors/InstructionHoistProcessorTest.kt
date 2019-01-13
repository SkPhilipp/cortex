package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import java.math.BigInteger

class InstructionHoistProcessorTest : ProcessorFuzzTest() {
    @Ignore
    @Test
    fun process() {
        // [PUSH 1, PUSH 4, JUMP, JUMP_DESTINATION, NOOP] ==> [NOOP, PUSH 4, JUMP, JUMP_DESTINATION, PUSH 1]
        val graphBuilder = GraphBuilder(listOf(
                InstructionHoistProcessor()
        ))
        val original = listOf(
                PUSH(1)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(InstructionHoistProcessor::class.java.simpleName)
                .headingParagraph(InstructionHoistProcessor::class.java.simpleName)
                .paragraph("Hoist instructions before JUMPs and JUMP_IFs to after JUMP_IFs and JUMP_DESTINATIONs where the JUMP_DESTINATIONs are only reachable through a single path.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                PUSH(1)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return InstructionHoistProcessor()
    }
}