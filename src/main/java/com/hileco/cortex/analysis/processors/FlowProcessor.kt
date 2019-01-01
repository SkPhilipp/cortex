package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphBlock
import com.hileco.cortex.analysis.GraphNode
import com.hileco.cortex.analysis.edges.EdgeFlow
import com.hileco.cortex.analysis.edges.EdgeFlowMapping
import com.hileco.cortex.analysis.edges.EdgeFlowType
import com.hileco.cortex.analysis.edges.EdgeFlowType.*
import com.hileco.cortex.analysis.edges.EdgeMapping
import com.hileco.cortex.instructions.calls.CALL
import com.hileco.cortex.instructions.calls.CALL_RETURN
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.stack.PUSH
import java.math.BigInteger

class FlowProcessor : Processor {
    private fun mapLinesToBlocksForNode(edgeMapping: EdgeMapping, edge: EdgeFlowMapping, graphBlock: GraphBlock, graphNode: GraphNode) {
        val line = graphNode.line
        edge.putLineMapping(line, graphBlock)
        edge.putLineMapping(line, graphNode)
        edgeMapping.parameters(graphNode)
                .filterNotNull()
                .forEach { mapLinesToBlocksForNode(edgeMapping, edge, graphBlock, it) }
    }

    override fun process(graph: Graph) {
        graph.edgeMapping.removeAll(EdgeFlowMapping::class.java)
        graph.edgeMapping.removeAll(EdgeFlow::class.java)

        val graphEdge = EdgeFlowMapping()
        val graphBlocks = graph.graphBlocks

        // map lines to blocks
        graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes.forEach { mapLinesToBlocksForNode(graph.edgeMapping, graphEdge, graphBlock, it) }
        }

        // other flow instructions
        graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes.asSequence()
                    .filter { it.isInstruction(FLOW_CLASSES_OTHERS) }
                    .forEach {
                        val blockStart = graphBlock.graphNodes[0].line
                        val instructionClass = it.instruction.get().javaClass
                        FLOW_TYPE_MAPPING[instructionClass]?.let { edgeFlowType ->
                            val edgeFlow = EdgeFlow(edgeFlowType, it.line, null)
                            graph.edgeMapping.add(it, edgeFlow)
                            graphEdge.map(edgeFlow)
                            if (it.isInstruction(*GUARANTEED_ENDS)) {
                                val blockPartEdgeFlow = EdgeFlow(BLOCK_PART, blockStart, it.line)
                                graph.edgeMapping.add(it, blockPartEdgeFlow)
                                graphEdge.map(blockPartEdgeFlow)
                            }
                        }
                    }
        }

        // map jumps to blocks
        graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes.asSequence()
                    .filter { it.isInstruction(FLOW_CLASSES_JUMPS) }
                    .filter { graph.edgeMapping.hasOneParameter(it, 0) { parameter -> parameter.instruction.get() is PUSH } }
                    .forEach {
                        val targetInstruction = graph.edgeMapping.parameters(it)[0]?.instruction?.get()
                        if (targetInstruction != null) {
                            targetInstruction as PUSH
                            val target = BigInteger(targetInstruction.bytes).toInt()
                            FLOW_TYPE_MAPPING[it.instruction.get().javaClass]?.let { edgeFlowType ->
                                val edgeFlow = EdgeFlow(edgeFlowType, it.line, target)
                                graph.edgeMapping.add(it, edgeFlow)
                                graphEdge.map(edgeFlow)
                            }
                        }
                    }
        }

        // map blocks to jumps
        graphBlocks.forEach { graphBlock ->
            val graphNodes = graphBlock.graphNodes
            if (!graphNodes.isEmpty()) {
                val graphBlockStart = graphNodes[0].line
                graphNodes.asSequence()
                        .filter { graphNode -> graphNode.isInstruction(FLOW_CLASSES_JUMPS) }
                        .forEach { graphNode ->
                            val edgeFlow = EdgeFlow(BLOCK_PART, graphBlockStart, graphNode.line)
                            graphEdge.map(edgeFlow)
                        }
            }
        }

        // map blocks to blocks
        val graphBlocksLimit = graphBlocks.size
        if (graphBlocksLimit >= 2) {
            for (i in 0 until graphBlocksLimit - 1) {
                val graphBlockA = graphBlocks[i]
                val graphBlockB = graphBlocks[i + 1]
                val graphNodesA = graphBlockA.graphNodes
                val graphNodesB = graphBlockB.graphNodes
                if (!graphNodesA.isEmpty()
                        && !graphNodesB.isEmpty()
                        && graphNodesA.none { graphNode -> graphNode.isInstruction(*GUARANTEED_ENDS) }) {
                    // TODO: Don't map blocks to block here; instead if a block contains no guaranteed ends
                    // TODO:   it should inherit all the mappings of the next block, continuing until either
                    // TODO:   the last block is reached or a block is found which does contain a guaranteed end
                    val graphNodeA = graphNodesA[0]
                    val graphNodeB = graphNodesB[0]
                    val edgeFlow = EdgeFlow(EdgeFlowType.BLOCK_END, graphNodeA.line, graphNodeB.line)
                    graphEdge.map(edgeFlow)
                    graph.edgeMapping.add(graphBlockA, edgeFlow)
                }
            }
        }

        // map program start
        if (graphBlocksLimit > 0) {
            val graphBlockStart = graphBlocks[0]
            val edgeFlow = EdgeFlow(EdgeFlowType.START, null, 0)
            graph.edgeMapping.add(graphBlockStart, edgeFlow)
            graphEdge.map(edgeFlow)
        }

        // map the last block's start to the program end (if such end is possible)
        if (graphBlocksLimit > 0) {
            val graphBlockEnd = graphBlocks[graphBlocks.size - 1]
            if (graphBlockEnd.graphNodes.none { graphNode -> graphNode.isInstruction(*GUARANTEED_ENDS) }) {
                val graphNode = graphBlockEnd.graphNodes[0]
                val edgeFlow = EdgeFlow(EdgeFlowType.END, graphNode.line, null)
                graph.edgeMapping.add(graphBlockEnd, edgeFlow)
                graphEdge.map(edgeFlow)
            }
        }

        graph.edgeMapping.add(graphEdge)
    }

    companion object {
        private val FLOW_CLASSES_JUMPS = setOf(JUMP::class.java, JUMP_IF::class.java)
        private val FLOW_CLASSES_OTHERS = setOf(HALT::class.java, EXIT::class.java, CALL_RETURN::class.java, CALL::class.java)
        private val GUARANTEED_ENDS = arrayOf(JUMP::class.java, HALT::class.java, EXIT::class.java, CALL_RETURN::class.java)
        private val FLOW_TYPE_MAPPING = mapOf(
                CALL::class.java to INSTRUCTION_CALL,
                CALL_RETURN::class.java to INSTRUCTION_CALL_RETURN,
                JUMP_IF::class.java to INSTRUCTION_JUMP_IF,
                JUMP::class.java to INSTRUCTION_JUMP,
                EXIT::class.java to INSTRUCTION_EXIT,
                HALT::class.java to INSTRUCTION_HALT
        )
    }
}
