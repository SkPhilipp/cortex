package com.hileco.cortex.instructions.stack


import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.constraints.expressions.Expression.Value
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_OVERFLOW
import com.hileco.cortex.instructions.stack.ExecutionVariable.*
import com.hileco.cortex.vm.ProgramConstants.Companion.STACK_LIMIT
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine

data class VARIABLE(val executionVariable: ExecutionVariable) : Instruction() {

    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        when (executionVariable) {
            ADDRESS_SELF -> {
                programContext.stack.push(programContext.program.address.toByteArray())
            }
            INSTRUCTION_POSITION -> {
                programContext.stack.push(programContext.instructionPosition.toBigInteger().toByteArray())
            }
            ADDRESS_CALLER -> {
                val address = if (virtualMachine.programs.size() > 1) virtualMachine.programs.peek(0).program.address else 0.toBigInteger()
                programContext.stack.push(address.toByteArray())
            }
            else -> {
                val value = virtualMachine.variables[executionVariable] ?: 0.toBigInteger()
                programContext.stack.push(value.toByteArray())
            }
        }
        if (programContext.stack.size() > STACK_LIMIT) {
            throw ProgramException(STACK_OVERFLOW)
        }
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        when (executionVariable) {
            ADDRESS_SELF -> {
                programContext.stack.push(Value(programContext.program.address.toLong()))
            }
            INSTRUCTION_POSITION -> {
                programContext.stack.push(Value(programContext.instructionPosition.toLong()))
            }
            ADDRESS_CALLER -> {
                val address = if (virtualMachine.programs.size() > 1) virtualMachine.programs.peek(0).program.address.toLong() else 0
                programContext.stack.push(Expression.Value(address))
            }
            else -> {
                virtualMachine.variables[executionVariable]
            }
        }
        if (programContext.stack.size() > STACK_LIMIT) {
            throw ProgramException(STACK_OVERFLOW)
        }
    }

    override fun toString(): String {
        return "VARIABLE $executionVariable"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is VARIABLE) executionVariable == other.executionVariable else false
    }

    override fun hashCode(): Int {
        return executionVariable.hashCode()
    }
}
