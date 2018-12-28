package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.ProgramException.Reason.JUMP_OUT_OF_BOUNDS
import com.hileco.cortex.instructions.ProgramException.Reason.JUMP_TO_ILLEGAL_INSTRUCTION
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class JumpIllegalProcessorTest : ProcessorFuzzTest() {
    @Test
    fun testprocessJumpToIllegalInstruction() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor(),
                JumpIllegalProcessor()
        ))
        val original = listOf(
                PUSH(BigInteger.ZERO.toByteArray()),
                JUMP(),
                PUSH(BigInteger.ONE.toByteArray())
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(JumpIllegalProcessor::class.simpleName!!)
                .headingParagraph(JumpIllegalProcessor::class.simpleName!!)
                .paragraph("Replaces JUMP instructions with HALTs when they are known to always jump to non-JUMP_DESTINATION instructions or" + " when they are known to jump out of bounds.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                PUSH(BigInteger.ZERO.toByteArray()),
                HALT(JUMP_TO_ILLEGAL_INSTRUCTION),
                PUSH(BigInteger.ONE.toByteArray())
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
                PUSH(BigInteger.TEN.toByteArray()),
                JUMP(),
                PUSH(BigInteger.ONE.toByteArray())
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()
        Assert.assertEquals(instructions, listOf(
                PUSH(BigInteger.TEN.toByteArray()),
                HALT(JUMP_OUT_OF_BOUNDS),
                PUSH(BigInteger.ONE.toByteArray())
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return JumpIllegalProcessor()
    }
}
