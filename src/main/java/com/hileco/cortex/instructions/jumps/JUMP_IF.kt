package com.hileco.cortex.instructions.jumps

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_UNDERFLOW
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
            throw ProgramException(STACK_UNDERFLOW)
        }
        val address = BigInteger(programContext.stack.pop()).toInt()
        val condition = BigInteger(programContext.stack.pop())
        if (condition.signum() == 1) {
            performJump(programContext, address)
        }
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        if (programContext.stack.size() < 2) {
            throw ProgramException(STACK_UNDERFLOW)
        }
        val addressExpression = programContext.stack.pop() as? Expression.Value
                ?: throw UnsupportedOperationException("Jumps to non-concrete addresses are not supported for symbolic execution")
        val conditionExpression = programContext.stack.pop() as? Expression.Value
                ?: throw UnsupportedOperationException("Jumps using non-concrete conditions should not be performed via this method")
        if (conditionExpression.constant > 0) {
            performJump(programContext, addressExpression.constant.toInt())
        }
    }

    companion object {
        val ADDRESS = StackParameter("address", 0)
        val CONDITION = StackParameter("condition", 1)
    }
}
