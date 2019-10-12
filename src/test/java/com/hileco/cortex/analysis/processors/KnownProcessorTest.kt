package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.stack.DUPLICATE
import com.hileco.cortex.instructions.stack.ExecutionVariable.INSTRUCTION_POSITION
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.instructions.stack.VARIABLE
import org.junit.Assert
import org.junit.Test

class KnownProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                KnownProcessor()
        ))
        val original = listOf(
                PUSH(1),
                PUSH(10),
                ADD()
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()
        Documentation.of(KnownProcessor::class.simpleName!!)
                .headingParagraph(KnownProcessor::class.simpleName!!)
                .paragraph("Precomputes instructions which modify only the stack and do not have any external dependencies.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)
        Assert.assertEquals(instructions, listOf(
                NOOP(),
                NOOP(),
                PUSH(11)
        ))
    }

    @Test
    fun processVariables() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                KnownProcessor()
        ))
        val original = listOf(
                NOOP(),
                NOOP(),
                VARIABLE(INSTRUCTION_POSITION),
                PUSH(10),
                ADD()
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()
        Assert.assertEquals(instructions, original)
    }

    @Test
    fun processMissing() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                KnownProcessor()
        ))
        val original = listOf(
                DUPLICATE(0),
                IS_ZERO()
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()
        Assert.assertEquals(instructions, original)
    }

    override fun fuzzTestableProcessor(): Processor {
        return KnownProcessor()
    }
}