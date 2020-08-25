package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.debug.NOOP
import com.hileco.cortex.vm.instructions.stack.PUSH
import com.hileco.cortex.vm.instructions.stack.SWAP
import org.junit.Assert
import org.junit.Test

class DeadSwapProcessorTest : ProcessorFuzzTest() {
    @Test
    fun testProcess() {
        val graphBuilder = GraphBuilder(listOf(
                DeadSwapProcessor()

        ))
        val original = listOf(
                PUSH(1.toBackedInteger()),
                PUSH(10.toBackedInteger()),
                SWAP(0, 0)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(DeadSwapProcessor::class.java.simpleName)
                .headingParagraph(DeadSwapProcessor::class.java.simpleName)
                .paragraph("Removes SWAP instructions which swap an element on the stack with the same element.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                PUSH(1.toBackedInteger()),
                PUSH(10.toBackedInteger()),
                NOOP()
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return DeadSwapProcessor()
    }
}