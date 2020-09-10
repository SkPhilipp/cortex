package com.hileco.cortex.analysis

import com.hileco.cortex.vm.instructions.Instruction

// TODO: Change this into PositionedInstruction
class GraphNode(var instruction: Instruction,
                val position: Int) {

    override fun toString(): String {
        return String.format("[%03d] %s", position, instruction)
    }
}
