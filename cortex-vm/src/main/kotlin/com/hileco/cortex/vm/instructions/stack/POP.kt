package com.hileco.cortex.vm.instructions.stack

import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionModifier
import com.hileco.cortex.vm.instructions.InstructionModifier.STACK
import com.hileco.cortex.vm.instructions.StackParameter

class POP : Instruction() {
    override val instructionModifiers: List<InstructionModifier>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(INPUT)

    companion object {
        val INPUT = StackParameter("input", 0)
    }
}