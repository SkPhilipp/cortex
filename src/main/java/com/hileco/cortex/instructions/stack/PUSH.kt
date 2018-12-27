package com.hileco.cortex.instructions.stack


import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_LIMIT_REACHED
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.VirtualMachine
import java.math.BigInteger

data class PUSH(val bytes: ByteArray) : Instruction() {

    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf()

    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        program.stack.push(bytes)
        if (program.stack.size() > process.stackLimit) {
            throw ProgramException(program, STACK_LIMIT_REACHED)
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
