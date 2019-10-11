package com.hileco.cortex.vm.instructions.jumps

import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionModifier
import com.hileco.cortex.vm.instructions.InstructionModifier.INSTRUCTION_POSITION
import com.hileco.cortex.vm.instructions.StackParameter

class JUMP : Instruction() {
    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(INSTRUCTION_POSITION)

    override val stackParameters: List<StackParameter>
        get() = listOf(ADDRESS)

    companion object {
        val ADDRESS = StackParameter("address", 0)
    }
}
