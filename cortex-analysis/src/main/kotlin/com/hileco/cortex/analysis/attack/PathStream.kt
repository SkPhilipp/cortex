package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.analysis.edges.FlowType
import com.hileco.cortex.vm.instructions.Instruction

class PathStream(private val instructions: List<Instruction>,
                 private val path: List<Flow>) {

    val instructionsAbsolute: Map<Int, Instruction>

    init {
        var position = 0
        instructionsAbsolute = instructions.map { instruction ->
            val result = position to instruction
            position += instruction.width
            result
        }.toMap()
    }

    fun asSequence() = sequence {
        path.forEachIndexed { index, flow ->
            if (flow.type == FlowType.PROGRAM_FLOW) {
                val next = if (index + 1 < path.size) path[index + 1] else null
                val source = flow.source
                val target = next?.source ?: flow.target!!
                var position = source
                while (position <= target) {
                    val instruction = instructionsAbsolute[position] ?: throw IndexOutOfBoundsException("Index $position is not a mapped instruction")
                    yield(PathStreamElement(instruction, position, flow, next))
                    position += instruction.width
                }
            }
        }
    }
}