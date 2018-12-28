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
        EdgeParameterConsumer.UTIL.clear(graph)
        EdgeParameters.UTIL.clear(graph)
        graph.graphBlocks.forEach { graphBlock ->
            val stack = LayeredStack<GraphNode>()
            val graphNodes = graphBlock.graphNodes
            for (graphNode in graphNodes) {
                val instruction = graphNode.instruction.get()
                if (instruction is JUMP_DESTINATION || instruction is SWAP) {
                    stack.clear()
                    continue
                }
                if (instruction is DUPLICATE) {
                    stack.clear()
                    stack.push(graphNode)
                    continue
                }
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
                        val parameter = stack[stackSize - i]!!
                        parameter.edges.add(EdgeParameterConsumer(graphNode))
                        parameters.add(parameter)
                    }
                    graphNode.edges.add(EdgeParameters(parameters))
                    stack.clear()
                }
                if (!instruction.stackAdds.isEmpty()) {
                    stack.push(graphNode)
                }
            }
        }
    }
}
