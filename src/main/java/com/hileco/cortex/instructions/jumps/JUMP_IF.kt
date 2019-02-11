package com.hileco.cortex.instructions.jumps

import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.INSTRUCTION_POSITION
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.VirtualMachine
import java.math.BigInteger

class JUMP_IF : JumpingInstruction() {
    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK, INSTRUCTION_POSITION)

    override val stackParameters: List<StackParameter>
        get() = listOf(ADDRESS, CONDITION)

    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        if (program.stack.size() < 2) {
            throw ProgramException(program, STACK_TOO_FEW_ELEMENTS)
        }
        val nextInstructionPosition = BigInteger(program.stack.pop()).toInt()
        val top = program.stack.pop()
        if (top.any { it > 0 }) {
            performJump(program, nextInstructionPosition)
        }
    }

    companion object {
        val ADDRESS = StackParameter("address", 0)
        val CONDITION = StackParameter("condition", 1)
    }
}
