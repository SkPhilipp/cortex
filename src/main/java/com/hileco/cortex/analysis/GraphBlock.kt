package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.edges.Edge
import com.hileco.cortex.instructions.Instruction
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class GraphBlock {
    val graphNodes: MutableList<GraphNode> = ArrayList()
    val edges: MutableList<Edge> = ArrayList()

    fun include(lineOffset: Int, instructions: List<AtomicReference<Instruction>>) {
        for (i in instructions.indices) {
            val instructionReference = instructions[i]
            val graphNode = GraphNode(instructionReference, lineOffset + i)
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
        return stringBuilder.toString()
    }
}
