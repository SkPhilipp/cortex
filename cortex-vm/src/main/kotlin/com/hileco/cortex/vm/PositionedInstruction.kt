package com.hileco.cortex.vm

import com.hileco.cortex.vm.instructions.Instruction

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