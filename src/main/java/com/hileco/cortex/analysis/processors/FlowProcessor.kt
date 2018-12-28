package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphBlock
import com.hileco.cortex.analysis.GraphNode
import com.hileco.cortex.analysis.edges.EdgeFlow
import com.hileco.cortex.analysis.edges.EdgeFlowMapping
import com.hileco.cortex.analysis.edges.EdgeFlowType
import com.hileco.cortex.analysis.edges.EdgeFlowType.*
import com.hileco.cortex.instructions.calls.CALL
import com.hileco.cortex.instructions.calls.CALL_RETURN
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.stack.PUSH
import java.math.BigInteger
import java.util.*

class FlowProcessor : Processor {
    private fun mapLinesToBlocksForNode(edge: EdgeFlowMapping, graphBlock: GraphBlock, graphNode: GraphNode) {
        val line = graphNode.line
        edge.putLineMapping(line, graphBlock)
        edge.putLineMapping(line, graphNode)
        graphNode.parameters().stream()
                .filter { Objects.nonNull(it) }
                .forEach { parameter ->
                    if (parameter != null) {
                        mapLinesToBlocksForNode(edge, graphBlock, parameter)
                    }
                }
    }

    override fun process(graph: Graph) {
        EdgeFlowMapping.UTIL.clear(graph)
        EdgeFlow.UTIL.clear(graph)

        val graphEdge = EdgeFlowMapping()
        val graphBlocks = graph.graphBlocks

        // map lines to blocks
        graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes
                    .forEach { graphNode -> mapLinesToBlocksForNode(graphEdge, graphBlock, graphNode) }
        }

        // other flow instructions
        graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes
                    .stream()
                    .filter { it.isInstruction(FLOW_CLASSES_OTHERS) }
                    .forEach { graphNode ->
                        val blockStart = graphBlock.graphNodes[0].line
                        val instructionClass = graphNode.instruction.get().javaClass
                        FLOW_TYPE_MAPPING[instructionClass]?.let {
                            val edgeFlow = EdgeFlow(it, graphNode.line, null)
                            graphNode.edges.add(edgeFlow)
                            graphEdge.map(edgeFlow)
                            if (graphNode.isInstruction(*GUARANTEED_ENDS)) {
                                val blockPartEdgeFlow = EdgeFlow(BLOCK_PART, blockStart, graphNode.line)
                                graphNode.edges.add(blockPartEdgeFlow)
                                graphEdge.map(blockPartEdgeFlow)
                            }
                        }
                    }
        }

        // map jumps to blocks
        graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes.stream()
                    .filter { graphNode -> graphNode.isInstruction(FLOW_CLASSES_JUMPS) }
                    .filter { graphNode -> graphNode.hasOneParameter(0) { parameter -> parameter.instruction.get() is PUSH } }
                    .forEach { graphNode ->
                        val targetPushInstruction = graphNode.parameters()[0].instruction.get() as PUSH
                        val target = BigInteger(targetPushInstruction.bytes).toInt()
                        FLOW_TYPE_MAPPING[graphNode.instruction.get().javaClass]?.let {
                            val edgeFlow = EdgeFlow(it, graphNode.line, target)
                            graphNode.edges.add(edgeFlow)
                            graphEdge.map(edgeFlow)
                        }
                    }
        }

        // map blocks to jumps
        graphBlocks.forEach { graphBlock ->
            val graphNodes = graphBlock.graphNodes
            if (!graphNodes.isEmpty()) {
                val graphBlockStart = graphNodes[0].line
                graphNodes.stream()
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
                        && graphNodesA.stream().noneMatch { graphNode -> graphNode.isInstruction(*GUARANTEED_ENDS) }) {
                    // TODO: Don't map blocks to block here; instead if a block contains no guaranteed ends
                    // TODO:   it should inherit all the mappings of the next block, continuing until either
                    // TODO:   the last block is reached or a block is found which does contain a guaranteed end
                    val graphNodeA = graphNodesA[0]
                    val graphNodeB = graphNodesB[0]
                    val edgeFlow = EdgeFlow(EdgeFlowType.BLOCK_END, graphNodeA.line, graphNodeB.line)
                    graphEdge.map(edgeFlow)
                    graphBlockA.edges.add(edgeFlow)
                }
            }
        }

        // map program start
        if (graphBlocksLimit > 0) {
            val graphBlockStart = graphBlocks[0]
            val edgeFlow = EdgeFlow(EdgeFlowType.START, null, 0)
            graphBlockStart.edges.add(edgeFlow)
            graphEdge.map(edgeFlow)
        }

        // map the last block's start to the program end (if such end is possible)
        if (graphBlocksLimit > 0) {
            val graphBlockEnd = graphBlocks[graphBlocks.size - 1]
            if (graphBlockEnd.graphNodes.stream().noneMatch { graphNode -> graphNode.isInstruction(*GUARANTEED_ENDS) }) {
                val graphNode = graphBlockEnd.graphNodes[0]
                val edgeFlow = EdgeFlow(EdgeFlowType.END, graphNode.line, null)
                graphBlockEnd.edges.add(edgeFlow)
                graphEdge.map(edgeFlow)
            }
        }

        graph.edges.add(graphEdge)
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
