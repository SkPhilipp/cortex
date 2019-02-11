package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphNode
import com.hileco.cortex.analysis.edges.EdgeParameterConsumer
import com.hileco.cortex.analysis.edges.EdgeParameters
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.stack.DUPLICATE
import com.hileco.cortex.instructions.stack.SWAP
import com.hileco.cortex.vm.layer.LayeredStack
import java.util.*


class ParameterProcessor : Processor {
    override fun process(graph: Graph) {
        graph.edgeMapping.removeAll(EdgeParameterConsumer::class.java)
        graph.edgeMapping.removeAll(EdgeParameters::class.java)
        graph.graphBlocks.forEach { graphBlock ->
            val stack = LayeredStack<GraphNode>()
            val graphNodes = graphBlock.graphNodes
            for (graphNode in graphNodes) {
                val instruction = graphNode.instruction
                if (instruction is JUMP_DESTINATION || instruction is SWAP) {
                    stack.clear()
                } else if (instruction is DUPLICATE) {
                    if (stack.size() <= instruction.topOffset) {
                        stack.clear()
                        stack.push(graphNode)
                    } else {
                        val parameter = stack[(stack.size() - 1) - instruction.topOffset]
                        graph.edgeMapping.add(parameter, EdgeParameterConsumer(graphNode))
                        graph.edgeMapping.add(graphNode, EdgeParameters(listOf(parameter)))
                        stack.clear()
                        stack.push(graphNode)
                    }
                } else {
                    val stackTakes = instruction.stackParameters.size
                    if (stackTakes > 0) {
                        val parameters = ArrayList<GraphNode?>()
                        val stackSize = stack.size()
                        val totalMissing = stackTakes - stackSize
                        for (i in 0 until totalMissing) {
                            parameters.add(null)
                        }
                        val remainingMissing = Math.min(stackTakes, stackTakes - totalMissing)
                        for (i in 0 until remainingMissing) {
                            val parameter = stack[stackSize - 1 - i]
                            graph.edgeMapping.add(parameter, EdgeParameterConsumer(graphNode))
                            parameters.add(parameter)
                        }
                        graph.edgeMapping.add(graphNode, EdgeParameters(parameters))
                        stack.clear()
                    }
                    if (!instruction.stackAdds.isEmpty()) {
                        stack.push(graphNode)
                    }
                }
            }
        }
    }
}
