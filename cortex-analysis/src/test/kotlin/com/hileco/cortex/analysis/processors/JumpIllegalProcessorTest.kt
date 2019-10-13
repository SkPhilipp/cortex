package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.ProgramException.Reason.JUMP_TO_ILLEGAL_INSTRUCTION
import com.hileco.cortex.vm.ProgramException.Reason.JUMP_TO_OUT_OF_BOUNDS
import com.hileco.cortex.vm.instructions.debug.HALT
import com.hileco.cortex.vm.instructions.jumps.JUMP
import com.hileco.cortex.vm.instructions.stack.PUSH
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
                PUSH(0),
                JUMP(),
                PUSH(1)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(JumpIllegalProcessor::class.java.simpleName)
                .headingParagraph(JumpIllegalProcessor::class.java.simpleName)
                .paragraph("Replaces JUMP instructions with HALTs when they are known to always jump to non-JUMP_DESTINATION instructions or when they are known to jump out of bounds.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                PUSH(0),
                HALT(JUMP_TO_ILLEGAL_INSTRUCTION),
                PUSH(1)
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
                PUSH(10),
                JUMP(),
                PUSH(1)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()
        Assert.assertEquals(instructions, listOf(
                PUSH(10),
                HALT(JUMP_TO_OUT_OF_BOUNDS),
                PUSH(1)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return JumpIllegalProcessor()
    }
}
