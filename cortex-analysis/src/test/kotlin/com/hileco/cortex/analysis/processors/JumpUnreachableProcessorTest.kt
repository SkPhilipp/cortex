package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.instructions.debug.NOOP
import com.hileco.cortex.vm.instructions.jumps.JUMP
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class JumpUnreachableProcessorTest : ProcessorFuzzTest() {
    @Test
    fun testProcessUnreachable() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor(),
                JumpUnreachableProcessor()
        ))
        val original = listOf(
                PUSH(3),
                JUMP(),
                JUMP_DESTINATION(),
                JUMP_DESTINATION()
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(JumpUnreachableProcessor::class.java.simpleName)
                .headingParagraph(JumpUnreachableProcessor::class.java.simpleName)
                .paragraph("Eliminates instructions which are never jumped to.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(listOf(
                PUSH(3),
                JUMP(),
                NOOP(),
                JUMP_DESTINATION()
        ), instructions)
        Assert.assertEquals(graph.graphBlocks.size, 2)
    }

    @Test
    fun testProcessUntouched() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor(),
                JumpUnreachableProcessor()
        ))
        val graph = graphBuilder.build(listOf(
                PUSH(2),
                JUMP(),
                JUMP_DESTINATION(),
                JUMP_DESTINATION()
        ))
        Assert.assertEquals(listOf(
                PUSH(2),
                JUMP(),
                JUMP_DESTINATION(),
                JUMP_DESTINATION()
        ), graph.toInstructions())
    }

    override fun fuzzTestableProcessor(): Processor {
        return JumpUnreachableProcessor()
    }
}