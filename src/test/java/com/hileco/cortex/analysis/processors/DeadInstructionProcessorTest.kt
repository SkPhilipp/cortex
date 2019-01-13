package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

@Ignore
class DeadInstructionProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        // a: [PUSH 10, PUSH 0] ==> [NOOP, NOOP]
        // b: [PUSH 10, PUSH 0, HALT WINNER] ==> [NOOP, NOOP, HALT WINNER]
        // b: [HALT WINNER, PUSH 10, PUSH 0] ==> No Change (see TrimEndProcessor)
        // c: [PUSH 10, PUSH 0, SAVE DISK, HALT WINNER] ==> No Change
        val graphBuilder = GraphBuilder(listOf(
                DeadInstructionProcessor()
        ))
        val original = listOf(
                PUSH(1)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(DeadInstructionProcessor::class.java.simpleName)
                .headingParagraph(DeadInstructionProcessor::class.java.simpleName)
                .paragraph("Removes instructions before a HALT or EXIT in the same block, which do not perform any kind of permanent modification.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                PUSH(1)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return DeadInstructionProcessor()
    }
}