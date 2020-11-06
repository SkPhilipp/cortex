package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.collections.toBackedInteger
import com.hileco.cortex.symbolic.instructions.debug.NOOP
import com.hileco.cortex.symbolic.instructions.jumps.JUMP
import com.hileco.cortex.symbolic.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.symbolic.instructions.stack.PUSH
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
                PUSH(3.toBackedInteger()),
                JUMP(),
                JUMP_DESTINATION(),
                JUMP_DESTINATION()
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Assert.assertEquals(listOf(
                PUSH(3.toBackedInteger()),
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
                PUSH(2.toBackedInteger()),
                JUMP(),
                JUMP_DESTINATION(),
                JUMP_DESTINATION()
        ))
        Assert.assertEquals(listOf(
                PUSH(2.toBackedInteger()),
                JUMP(),
                JUMP_DESTINATION(),
                JUMP_DESTINATION()
        ), graph.toInstructions())
    }

    override fun fuzzTestableProcessor(): Processor {
        return JumpUnreachableProcessor()
    }
}