package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.edges.EdgeFlowMapping
import com.hileco.cortex.analysis.edges.EdgeFlowType.INSTRUCTION_JUMP
import com.hileco.cortex.instructions.ProgramException.Reason.JUMP_OUT_OF_BOUNDS
import com.hileco.cortex.instructions.ProgramException.Reason.JUMP_TO_ILLEGAL_INSTRUCTION
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION

class JumpIllegalProcessor : Processor {
    override fun process(graph: Graph) {
        EdgeFlowMapping.UTIL.findAny(graph)?.let {
            it.flowsFromSource.forEach { _, edgeFlows ->
                val jumpEdgeFlows = edgeFlows.filter { edgeFlow -> edgeFlow.type == INSTRUCTION_JUMP }
                if (jumpEdgeFlows.size == 1) {
                    val onlyJumpEdgeFlow = jumpEdgeFlows.first()
                    val sourceNode = it.nodeLineMapping[onlyJumpEdgeFlow.source]
                    if (sourceNode != null) {
                        val targetedNode = it.nodeLineMapping[onlyJumpEdgeFlow.target]
                        if (targetedNode == null) {
                            sourceNode.instruction.set(HALT(JUMP_OUT_OF_BOUNDS))
                        } else if (targetedNode.instruction.get() !is JUMP_DESTINATION) {
                            sourceNode.instruction.set(HALT(JUMP_TO_ILLEGAL_INSTRUCTION))
                        }
                    }
                }
            }
        }
    }
}
