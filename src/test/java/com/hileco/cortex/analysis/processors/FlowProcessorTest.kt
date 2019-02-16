package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.edges.FlowMapping
import com.hileco.cortex.analysis.edges.FlowType.INSTRUCTION_JUMP
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class FlowProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor()
        ))
        val original = listOf(
                PUSH(3),
                JUMP(),
                PUSH(10),
                JUMP_DESTINATION(),
                PUSH(1))
        val graph = graphBuilder.build(original)
        Documentation.of(FlowProcessor::class.simpleName!!)
                .headingParagraph(FlowProcessor::class.simpleName!!)
                .paragraph("Adds edges describing the program flow, this includes JUMP and JUMP_IFs where jump address information is known ahead of time.")

        val flowMapping = graph.edgeMapping.get(FlowMapping::class.java).first()
        val fromInstruction1 = flowMapping.flowsFromSource[1]!!.first()
        val toInstruction3 = flowMapping.flowsToTarget[3]!!.first()
        Assert.assertEquals(fromInstruction1.target, 3)
        Assert.assertEquals(fromInstruction1.type, INSTRUCTION_JUMP)
        Assert.assertEquals(toInstruction3.target, 3)
        Assert.assertEquals(toInstruction3.type, INSTRUCTION_JUMP)
    }

    override fun fuzzTestableProcessor(): Processor {
        return FlowProcessor()
    }
}