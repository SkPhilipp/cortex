package com.hileco.cortex.analysis

import com.hileco.cortex.instructions.Instruction
import java.util.*

class GraphBlock {
    val graphNodes: MutableList<GraphNode> = ArrayList()

    fun include(lineOffset: Int, instructions: List<Instruction>) {
        for (i in instructions.indices) {
            val graphNode = GraphNode(instructions[i], lineOffset + i)
            graphNodes.add(graphNode)
        }
    }

    fun append(other: GraphBlock) {
        graphNodes.addAll(other.graphNodes)
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (graphNode in graphNodes) {
            stringBuilder.append(graphNode)
            stringBuilder.append('\n')
        }
        return "$stringBuilder"
    }
}
