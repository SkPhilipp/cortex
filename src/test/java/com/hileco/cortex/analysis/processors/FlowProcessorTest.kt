package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.edges.EdgeFlowMapping
import com.hileco.cortex.analysis.edges.EdgeFlowType.INSTRUCTION_JUMP
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class FlowProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor()
        ))
        val original = listOf(
                PUSH(BigInteger.valueOf(3).toByteArray()),
                JUMP(),
                PUSH(BigInteger.valueOf(10).toByteArray()),
                JUMP_DESTINATION(),
                PUSH(BigInteger.valueOf(1).toByteArray()))
        val graph = graphBuilder.build(original)
        Documentation.of(FlowProcessor::class.simpleName!!)
                .headingParagraph(FlowProcessor::class.simpleName!!)
                .paragraph("Adds edges describing the program flow, this includes JUMP and JUMP_IFs where jump address information is known ahead of time.")

        val edgeFlowMapping = EdgeFlowMapping.UTIL.findAny(graph)!!
        val fromInstruction1 = edgeFlowMapping.flowsFromSource[1]!!.stream().findFirst().orElseThrow()
        val toInstruction3 = edgeFlowMapping.flowsToTarget[3]!!.stream().findFirst().orElseThrow()
        Assert.assertEquals(fromInstruction1.target, 3)
        Assert.assertEquals(fromInstruction1.type, INSTRUCTION_JUMP)
        Assert.assertEquals(toInstruction3.target, 3)
        Assert.assertEquals(toInstruction3.type, INSTRUCTION_JUMP)
    }

    override fun fuzzTestableProcessor(): Processor {
        return FlowProcessor()
    }
}