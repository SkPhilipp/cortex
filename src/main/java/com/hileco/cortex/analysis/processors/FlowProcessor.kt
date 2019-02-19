package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphBlock
import com.hileco.cortex.analysis.GraphNode
import com.hileco.cortex.analysis.edges.EdgeMapping
import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.analysis.edges.FlowMapping
import com.hileco.cortex.analysis.edges.FlowType.*
import com.hileco.cortex.instructions.calls.CALL_RETURN
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.stack.PUSH
import java.math.BigInteger

class FlowProcessor : Processor {
    private fun mapLinesToBlocksForNode(edgeMapping: EdgeMapping, edge: FlowMapping, graphBlock: GraphBlock, graphNode: GraphNode) {
        val line = graphNode.line
        edge.putLineMapping(line, graphBlock)
        edge.putLineMapping(line, graphNode)
        edgeMapping.parameters(graphNode)
                .filterNotNull()
                .forEach { mapLinesToBlocksForNode(edgeMapping, edge, graphBlock, it) }
    }

    override fun process(graph: Graph) {
        graph.edgeMapping.removeAll(FlowMapping::class.java)
        graph.edgeMapping.removeAll(Flow::class.java)

        val graphEdge = FlowMapping()
        val graphBlocks = graph.graphBlocks

        // map lines to blocks
        graphBlocks.forEach { graphBlock ->
            graphBlock.graphNodes.forEach { mapLinesToBlocksForNode(graph.edgeMapping, graphEdge, graphBlock, it) }
        }

        // map INSTRUCTION_JUMP and INSTRUCTION_JUMP_IF
        graphBlocks.asSequence()
                .flatMap { it.graphNodes.asSequence() }
                .filter { it.instruction::class.java in FLOW_CLASSES_JUMPS }
                .filter { graph.edgeMapping.hasOneParameter(it, 0) { parameter -> parameter.instruction is PUSH } }
                .forEach {
                    graph.edgeMapping.parameters(it).elementAt(0)?.let { parameterNode ->
                        val targetInstruction = parameterNode.instruction as PUSH
                        val target = BigInteger(targetInstruction.bytes).toInt()
                        when (it.instruction) {
                            is JUMP -> {
                                val flow = Flow(INSTRUCTION_JUMP, it.line, target)
                                graph.edgeMapping.add(it, flow)
                                graphEdge.map(flow)
                            }
                            is JUMP_IF -> {
                                val flow = Flow(INSTRUCTION_JUMP_IF, it.line, target)
                                graph.edgeMapping.add(it, flow)
                                graphEdge.map(flow)
                            }
                        }
                    }
                }

        // map INSTRUCTION_JUMP_DYNAMIC and INSTRUCTION_JUMP_IF_DYNAMIC
        graphBlocks.asSequence()
                .flatMap { it.graphNodes.asSequence() }
                .filter { it.instruction::class.java in FLOW_CLASSES_JUMPS }
                .filter { !graph.edgeMapping.hasOneParameter(it, 0) { parameter -> parameter.instruction is PUSH } }
                .forEach {
                    when (it.instruction) {
                        is JUMP -> {
                            val flow = Flow(INSTRUCTION_JUMP_DYNAMIC, it.line, null)
                            graph.edgeMapping.add(it, flow)
                            graphEdge.map(flow)
                        }
                        is JUMP_IF -> {
                            val flow = Flow(INSTRUCTION_JUMP_IF_DYNAMIC, it.line, null)
                            graph.edgeMapping.add(it, flow)
                            graphEdge.map(flow)
                        }
                    }
                }

        // map PROGRAM_END
        graphBlocks.asSequence()
                .flatMap { it.graphNodes.asSequence() }
                .filter { it.instruction::class.java in PROGRAM_ENDS }
                .forEach {
                    val flow = Flow(PROGRAM_END, it.line, null)
                    graph.edgeMapping.add(it, flow)
                    graphEdge.map(flow)
                }
        val lastBlock = graphBlocks.lastOrNull()
        if (lastBlock != null && lastBlock.graphNodes.asSequence().none { it.instruction::class.java in PROGRAM_ENDS }) {
            val lastNode = lastBlock.graphNodes.last()
            val flow = Flow(PROGRAM_END, lastNode.line, null)
            graph.edgeMapping.add(lastNode, flow)
            graphEdge.map(flow)
        }

        // map PROGRAM_FLOW
        graphBlocks.forEachIndexed { indexA, graphBlockA ->
            var limit = graphBlocks.size - 1
            graphBlocks.forEachIndexed { indexB, graphBlockB ->
                if (indexB in indexA .. limit) {
                    val guaranteedEnd = graphBlockB.graphNodes.firstOrNull { it.instruction::class.java in GUARANTEED_ENDS }
                    if (guaranteedEnd != null) {
                        val flow = Flow(PROGRAM_FLOW, graphBlockA.graphNodes.first().line, guaranteedEnd.line)
                        graphEdge.map(flow)
                        graph.edgeMapping.add(graphBlockA, flow)
                        limit = indexB
                    } else if (indexB == limit) {
                        val flow = Flow(PROGRAM_FLOW, graphBlockA.graphNodes.first().line, graphBlockB.graphNodes.last().line)
                        graphEdge.map(flow)
                        graph.edgeMapping.add(graphBlockA, flow)
                    }
                }
            }
        }

        graph.edgeMapping.add(graphEdge)
    }

    companion object {
        private val GUARANTEED_ENDS = setOf(JUMP::class.java, HALT::class.java, EXIT::class.java, CALL_RETURN::class.java)
        private val PROGRAM_ENDS = setOf(CALL_RETURN::class.java, EXIT::class.java, HALT::class.java)
        private val FLOW_CLASSES_JUMPS = setOf(JUMP::class.java, JUMP_IF::class.java)
    }
}
