package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class DeadEndProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val graphBuilder = GraphBuilder(listOf(
                DeadEndProcessor()
        ))
        val original = listOf(
                EXIT(),
                PUSH(10),
                PUSH(1),
                JUMP_DESTINATION(),
                PUSH(10),
                PUSH(1)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(DeadEndProcessor::class.simpleName!!)
                .headingParagraph(DeadEndProcessor::class.simpleName!!)
                .paragraph("Removes any instructions within the same jump-reachable block following another instruction that guarantees the instructions will not be reached.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                EXIT(),
                NOOP(),
                NOOP(),
                JUMP_DESTINATION(),
                PUSH(10),
                PUSH(1)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return DeadEndProcessor()
    }
}