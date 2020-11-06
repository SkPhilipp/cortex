package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.collections.backed.BackedInteger.Companion.ONE_32
import com.hileco.cortex.collections.backed.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.collections.backed.toBackedInteger
import com.hileco.cortex.symbolic.ProgramException.Reason.JUMP_TO_ILLEGAL_INSTRUCTION
import com.hileco.cortex.symbolic.ProgramException.Reason.JUMP_TO_OUT_OF_BOUNDS
import com.hileco.cortex.symbolic.instructions.debug.HALT
import com.hileco.cortex.symbolic.instructions.jumps.JUMP
import com.hileco.cortex.symbolic.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class JumpIllegalProcessorTest : ProcessorFuzzTest() {
    @Test
    fun testProcessJumpToIllegalInstruction() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor(),
                JumpIllegalProcessor()
        ))
        val original = listOf(
                PUSH(ZERO_32),
                JUMP(),
                PUSH(ONE_32)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Assert.assertEquals(instructions, listOf(
                PUSH(ZERO_32),
                HALT(JUMP_TO_ILLEGAL_INSTRUCTION),
                PUSH(ONE_32)
        ))
    }

    @Test
    fun testProcessJumpOutOfBounds() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor(),
                JumpIllegalProcessor()
        ))
        val original = listOf(
                PUSH(10.toBackedInteger()),
                JUMP(),
                PUSH(ONE_32)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()
        Assert.assertEquals(instructions, listOf(
                PUSH(10.toBackedInteger()),
                HALT(JUMP_TO_OUT_OF_BOUNDS),
                PUSH(ONE_32)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return JumpIllegalProcessor()
    }
}
