package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.edges.FlowMapping
import com.hileco.cortex.analysis.edges.FlowType.INSTRUCTION_JUMP
import com.hileco.cortex.collections.backed.BackedInteger.Companion.ONE_32
import com.hileco.cortex.collections.backed.toBackedInteger
import com.hileco.cortex.symbolic.instructions.jumps.JUMP
import com.hileco.cortex.symbolic.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.symbolic.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class FlowProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val instructions = listOf(
                PUSH(3.toBackedInteger()),
                JUMP(),
                PUSH(10.toBackedInteger()),
                JUMP_DESTINATION(),
                PUSH(ONE_32))
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(instructions)
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