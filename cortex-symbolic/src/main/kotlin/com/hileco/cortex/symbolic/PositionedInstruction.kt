package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.instructions.Instruction

data class PositionedInstruction(
        /**
         * Position of an instruction as a character in bytecode.
         */
        val absolutePosition: Int,
        /**
         * Position of an instruction as an effective instruction in a list for a program.
         */
        val relativePosition: Int,
        val instruction: Instruction
)