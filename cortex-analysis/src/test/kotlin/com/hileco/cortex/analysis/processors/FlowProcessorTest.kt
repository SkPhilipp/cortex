package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.edges.FlowMapping
import com.hileco.cortex.analysis.edges.FlowType.INSTRUCTION_JUMP
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.InstructionsBuilder
import com.hileco.cortex.vm.instructions.jumps.JUMP
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.instructions.stack.PUSH
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
                PUSH(1.toBackedInteger()))
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(instructions)
        Documentation.of(FlowProcessor::class.java.simpleName)
                .headingParagraph(FlowProcessor::class.java.simpleName)
                .paragraph("Adds edges describing the program flow, this includes JUMP and JUMP_IFs where jump address information is known ahead of time.")

        val flowMapping = graph.edgeMapping.get(FlowMapping::class.java).first()
        val fromInstruction1 = flowMapping.flowsFromSource[1]!!.first()
        val toInstruction3 = flowMapping.flowsToTarget[3]!!.first()
        Assert.assertEquals(fromInstruction1.target, 3)
        Assert.assertEquals(fromInstruction1.type, INSTRUCTION_JUMP)
        Assert.assertEquals(toInstruction3.target, 3)
        Assert.assertEquals(toInstruction3.type, INSTRUCTION_JUMP)
    }

    @Test
    fun processDynamicJumps() {
        val instructions = with(InstructionsBuilder()) {
            internalFunctionCall("cube", {
                push(123.toBackedInteger())
            })
            internalFunction("cube", {
                internalFunctionCall("square", {
                    duplicate(1)
                })
                multiply()
            })
            internalFunction("square", {
                duplicate()
                multiply()
            })
            build()
        }
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(instructions)
        val flowMapping = graph.edgeMapping.get(FlowMapping::class.java).first()
        Assert.assertEquals(flowMapping.flows.count { it.type.dynamic }, 2)
    }

    override fun fuzzTestableProcessor(): Processor {
        return FlowProcessor()
    }
}