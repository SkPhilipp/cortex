package com.hileco.cortex.analysis

import com.hileco.cortex.instructions.Instruction

class GraphNode(var instruction: Instruction,
                val line: Int) {

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(String.format("[%03d]", line))
        stringBuilder.append(instruction)
        return "$stringBuilder"
    }
}
