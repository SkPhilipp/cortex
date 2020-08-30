package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ONE_32
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.debug.NOOP
import com.hileco.cortex.vm.instructions.jumps.EXIT
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.instructions.stack.PUSH
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
                PUSH(10.toBackedInteger()),
                PUSH(ONE_32),
                JUMP_DESTINATION(),
                PUSH(10.toBackedInteger()),
                PUSH(ONE_32)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(DeadEndProcessor::class.java.simpleName)
                .headingParagraph(DeadEndProcessor::class.java.simpleName)
                .paragraph("Removes any instructions within the same jump-reachable block following another instruction that guarantees the instructions will not be reached.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                EXIT(),
                NOOP(),
                NOOP(),
                JUMP_DESTINATION(),
                PUSH(10.toBackedInteger()),
                PUSH(ONE_32)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return DeadEndProcessor()
    }
}