package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphNode
import com.hileco.cortex.analysis.edges.EdgeParameterConsumer
import com.hileco.cortex.analysis.edges.EdgeParameters
import com.hileco.cortex.collections.layer.LayeredVmStack
import com.hileco.cortex.symbolic.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.symbolic.instructions.stack.DUPLICATE
import com.hileco.cortex.symbolic.instructions.stack.SWAP
import java.util.*

class ParameterProcessor : Processor {
    override fun process(graph: Graph) {
        graph.edgeMapping.removeAll(EdgeParameterConsumer::class.java)
        graph.edgeMapping.removeAll(EdgeParameters::class.java)
        graph.graphBlocks.forEach { graphBlock ->
            val stack = LayeredVmStack<GraphNode>()
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
                        val parameter = stack.peek(instruction.topOffset)
                        graph.edgeMapping.add(parameter, EdgeParameterConsumer(graphNode))
                        graph.edgeMapping.add(graphNode, EdgeParameters(listOf(parameter)))
                        stack.push(graphNode)
                    }
                } else {
                    val stackParametersSize = instruction.stackParameters.size
                    if (stackParametersSize > 0) {
                        val parameters = ArrayList<GraphNode?>()
                        val stackSize = stack.size()
                        val totalMissing = stackParametersSize - stackSize
                        for (i in 0 until totalMissing) {
                            parameters.add(null)
                        }
                        val totalAvailable = stackParametersSize.coerceAtMost(stackParametersSize - totalMissing)
                        for (i in 0 until totalAvailable) {
                            val parameter = stack.peek(i)
                            graph.edgeMapping.add(parameter, EdgeParameterConsumer(graphNode))
                            parameters.add(parameter)
                        }
                        graph.edgeMapping.add(graphNode, EdgeParameters(parameters))
                        for (i in 0 until totalAvailable) {
                            stack.pop()
                        }
                    }
                    // TODO: Verify that this is correct for SWAP
                    if (instruction.stackAdds.isNotEmpty()) {
                        stack.push(graphNode)
                    }
                }
            }
        }
    }
}
