package com.hileco.cortex.instructions.jumps

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_UNDERFLOW
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.INSTRUCTION_POSITION
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import java.math.BigInteger

class JUMP : JumpingInstruction() {
    override val instructionModifiers: List<ProgramZone>
        get() = listOf(INSTRUCTION_POSITION)

    override val stackParameters: List<StackParameter>
        get() = listOf(ADDRESS)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() < 1) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        val nextInstructionPosition = BigInteger(programContext.stack.pop()).toInt()
        performJump(programContext, nextInstructionPosition)
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        if (programContext.stack.size() < 1) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        val targetExpression = programContext.stack.pop() as? Expression.Value
                ?: throw UnsupportedOperationException("Jumps to non-concrete targets are not supported for symbolic execution")
        performJump(programContext, targetExpression.constant.toInt())
    }

    companion object {
        val ADDRESS = StackParameter("address", 0)
    }
}
