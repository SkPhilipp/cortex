package com.hileco.cortex.instructions.jumps

import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.INSTRUCTION_POSITION
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import java.math.BigInteger

class JUMP_IF : JumpingInstruction() {
    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK, INSTRUCTION_POSITION)

    override val stackParameters: List<StackParameter>
        get() = listOf(ADDRESS, CONDITION)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() < 2) {
            throw ProgramException(STACK_TOO_FEW_ELEMENTS)
        }
        val nextInstructionPosition = BigInteger(programContext.stack.pop()).toInt()
        val top = programContext.stack.pop()
        if (top.any { it > 0 }) {
            performJump(programContext, nextInstructionPosition)
        }
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        throw UnsupportedOperationException()
    }

    companion object {
        val ADDRESS = StackParameter("address", 0)
        val CONDITION = StackParameter("condition", 1)
    }
}
