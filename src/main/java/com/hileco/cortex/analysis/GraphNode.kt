package com.hileco.cortex.analysis

import com.hileco.cortex.instructions.Instruction

class GraphNode(var instruction: Instruction,
                val line: Int) {

    override fun toString(): String {
        return String.format("[%03d] %s", line, instruction)
    }
}
