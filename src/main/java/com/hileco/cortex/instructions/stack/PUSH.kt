package com.hileco.cortex.instructions.stack


import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_OVERFLOW
import com.hileco.cortex.vm.ProgramConstants.Companion.STACK_LIMIT
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import java.math.BigInteger

data class PUSH(val bytes: ByteArray) : Instruction() {

    constructor(value: Long) : this(value.toBigInteger().toByteArray())

    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    val value: Long
        get() = BigInteger(bytes).toLong()

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        programContext.stack.push(bytes)
        if (programContext.stack.size() > STACK_LIMIT) {
            throw ProgramException(STACK_OVERFLOW)
        }
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        programContext.stack.push(Expression.Value(BigInteger(bytes).toLong()))
        if (programContext.stack.size() > STACK_LIMIT) {
            throw ProgramException(STACK_OVERFLOW)
        }
    }

    override fun toString(): String {
        return "PUSH ${BigInteger(bytes)}"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is PUSH) bytes.contentEquals(other.bytes) else false
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}
