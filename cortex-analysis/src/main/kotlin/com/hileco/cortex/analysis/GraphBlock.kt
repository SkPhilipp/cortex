package com.hileco.cortex.analysis

import com.hileco.cortex.symbolic.instructions.Instruction
import java.util.*

class GraphBlock {
    val graphNodes: MutableList<GraphNode> = ArrayList()

    fun include(initialOffset: Int, instructions: List<Instruction>) {
        var offset = initialOffset
        for (i in instructions.indices) {
            val instruction = instructions[i]
            val graphNode = GraphNode(instruction, offset)
            offset += instruction.width
            graphNodes.add(graphNode)
        }
    }

    fun append(other: GraphBlock) {
        graphNodes.addAll(other.graphNodes)
    }

    override fun toString(): String {
        return graphNodes.joinToString { "$it\n" }
    }
}
