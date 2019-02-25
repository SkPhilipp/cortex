package com.hileco.cortex.instructions.stack


import com.hileco.cortex.constraints.expressions.Expression.Value
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_OVERFLOW
import com.hileco.cortex.instructions.stack.ExecutionVariable.ADDRESS
import com.hileco.cortex.instructions.stack.ExecutionVariable.INSTRUCTION_POSITION
import com.hileco.cortex.vm.ProgramConstants.Companion.STACK_LIMIT
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import org.apache.batik.svggen.font.table.Table.name

data class VARIABLE(val executionVariable: ExecutionVariable) : Instruction() {

    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = when(executionVariable) {
            ADDRESS -> listOf(ProgramZone.STACK)
            INSTRUCTION_POSITION -> listOf(ProgramZone.STACK, ProgramZone.INSTRUCTION_POSITION)
        }

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        when (executionVariable) {
            ADDRESS -> {
                programContext.stack.push(programContext.program.address.toByteArray())
            }
            INSTRUCTION_POSITION -> {
                programContext.stack.push(programContext.instructionPosition.toBigInteger().toByteArray())
            }
        }
        if (programContext.stack.size() > STACK_LIMIT) {
            throw ProgramException(STACK_OVERFLOW)
        }
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        when (executionVariable) {
            ADDRESS -> {
                programContext.stack.push(Value(programContext.program.address.toLong()))
            }
            INSTRUCTION_POSITION -> {
                programContext.stack.push(Value(programContext.instructionPosition.toLong()))
            }
        }
        if (programContext.stack.size() > STACK_LIMIT) {
            throw ProgramException(STACK_OVERFLOW)
        }
    }

    override fun toString(): String {
        return "VARIABLE $name"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is VARIABLE) executionVariable == other.executionVariable else false
    }

    override fun hashCode(): Int {
        return executionVariable.hashCode()
    }
}
