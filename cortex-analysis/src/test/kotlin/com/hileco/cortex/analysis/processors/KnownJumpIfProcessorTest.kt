package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.instructions.debug.NOOP
import com.hileco.cortex.vm.instructions.jumps.JUMP
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class KnownJumpIfProcessorTest : ProcessorFuzzTest() {
    @Test
    fun testProcess() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                KnownJumpIfProcessor()

        ))
        val original = listOf(
                PUSH(1),
                PUSH(10),
                JUMP_IF()
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(KnownJumpIfProcessor::class.java.simpleName)
                .headingParagraph(KnownJumpIfProcessor::class.java.simpleName)
                .paragraph("Replaces JUMP_IF instructions with JUMP or NOOP instructions where the condition is known ahead of time.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                NOOP(),
                PUSH(10),
                JUMP()
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return KnownJumpIfProcessor()
    }
}