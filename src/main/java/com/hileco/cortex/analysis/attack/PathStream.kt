package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.analysis.edges.FlowType
import com.hileco.cortex.instructions.Instruction

class PathStream(private val instructions: List<Instruction>,
                 private val path: List<Flow>) {
    fun asSequence() = sequence {
        path.forEachIndexed { index, flow ->
            if (flow.type == FlowType.PROGRAM_FLOW) {
                val next = if (index + 1 < path.size) path[index + 1] else null
                val source = flow.source
                val target = next?.source ?: flow.target!!
                for (line in source..target) {
                    val instruction = instructions[line]
                    yield(PathStreamElement(instruction, line, flow, next))
                }
            }
        }
    }
}