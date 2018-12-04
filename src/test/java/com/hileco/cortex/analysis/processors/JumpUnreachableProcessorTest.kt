package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

import java.math.BigInteger

class JumpUnreachableProcessorTest : ProcessorFuzzTest() {

    @Test
    fun testProcessUnreachable() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor(),
                JumpUnreachableProcessor()
        ))
        val original = listOf(
                PUSH(BigInteger.valueOf(3).toByteArray()),
                JUMP(),
                JUMP_DESTINATION(),
                JUMP_DESTINATION()
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(JumpUnreachableProcessor::class.simpleName!!)
                .headingParagraph(JumpUnreachableProcessor::class.simpleName!!)
                .paragraph("Eliminates instructions which are never jumped to.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                PUSH(BigInteger.valueOf(3).toByteArray()),
                JUMP(),
                NOOP(),
                JUMP_DESTINATION()
        ))
    }

    @Test
    fun testProcessUntouched() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor(),
                JumpUnreachableProcessor()
        ))
        val graph = graphBuilder.build(listOf(
                PUSH(BigInteger.valueOf(2).toByteArray()),
                JUMP(),
                JUMP_DESTINATION(),
                JUMP_DESTINATION()
        ))
        Assert.assertEquals(graph.toInstructions(), listOf(
                PUSH(BigInteger.valueOf(2).toByteArray()),
                JUMP(),
                JUMP_DESTINATION(),
                JUMP_DESTINATION()
        ))
    }

    internal override fun fuzzTestableProcessor(): Processor {
        return JumpUnreachableProcessor()
    }
}