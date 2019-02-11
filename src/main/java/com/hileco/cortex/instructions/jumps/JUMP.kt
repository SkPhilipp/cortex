package com.hileco.cortex.instructions.jumps

import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.INSTRUCTION_POSITION
import com.hileco.cortex.vm.VirtualMachine
import java.math.BigInteger

class JUMP : JumpingInstruction() {
    override val instructionModifiers: List<ProgramZone>
        get() = listOf(INSTRUCTION_POSITION)

    override val stackParameters: List<StackParameter>
        get() = listOf(ADDRESS)

    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        if (program.stack.size() < 1) {
            throw ProgramException(program, STACK_TOO_FEW_ELEMENTS)
        }
        val nextInstructionPosition = BigInteger(program.stack.pop()).toInt()
        performJump(program, nextInstructionPosition)
    }

    companion object {
        val ADDRESS = StackParameter("address", 0)
    }
}
