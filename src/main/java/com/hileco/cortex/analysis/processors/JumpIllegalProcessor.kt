package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.edges.EdgeFlowMapping
import com.hileco.cortex.analysis.edges.EdgeFlowType.INSTRUCTION_JUMP
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION

class JumpIllegalProcessor : Processor {
    override fun process(graph: Graph) {
        val edgeFlowMapping = graph.edgeMapping.get(EdgeFlowMapping::class.java).single()
        edgeFlowMapping.flowsFromSource.values.forEach { edgeFlows ->
            val onlyJumpEdgeFlow = edgeFlows.singleOrNull { edgeFlow -> edgeFlow.type == INSTRUCTION_JUMP }
            if (onlyJumpEdgeFlow != null) {
                val sourceNode = edgeFlowMapping.nodeLineMapping[onlyJumpEdgeFlow.source]
                if (sourceNode != null) {
                    val targetedNode = edgeFlowMapping.nodeLineMapping[onlyJumpEdgeFlow.target]
                    if (targetedNode == null) {
                        sourceNode.instruction = HALT(ProgramException.Reason.JUMP_OUT_OF_BOUNDS)
                    } else if (targetedNode.instruction !is JUMP_DESTINATION) {
                        sourceNode.instruction = HALT(ProgramException.Reason.JUMP_TO_ILLEGAL_INSTRUCTION)
                    }
                }
            }
        }
    }
}
