package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.edges.FlowMapping
import com.hileco.cortex.analysis.edges.FlowType.INSTRUCTION_JUMP
import com.hileco.cortex.instructions.ProgramException.Reason.JUMP_TO_ILLEGAL_INSTRUCTION
import com.hileco.cortex.instructions.ProgramException.Reason.JUMP_TO_OUT_OF_BOUNDS
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION

class JumpIllegalProcessor : Processor {
    override fun process(graph: Graph) {
        val flowMapping = graph.edgeMapping.get(FlowMapping::class.java).single()
        flowMapping.flowsFromSource.values.forEach { flows ->
            val onlyJumpFlow = flows.singleOrNull { flow -> flow.type == INSTRUCTION_JUMP }
            if (onlyJumpFlow != null) {
                val sourceNode = flowMapping.nodeLineMapping[onlyJumpFlow.source]
                if (sourceNode != null) {
                    val targetedNode = flowMapping.nodeLineMapping[onlyJumpFlow.target]
                    if (targetedNode == null) {
                        sourceNode.instruction = HALT(JUMP_TO_OUT_OF_BOUNDS)
                    } else if (targetedNode.instruction !is JUMP_DESTINATION) {
                        sourceNode.instruction = HALT(JUMP_TO_ILLEGAL_INSTRUCTION)
                    }
                }
            }
        }
    }
}
