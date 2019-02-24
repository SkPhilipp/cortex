package com.hileco.cortex.instructions.math

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_UNDERFLOW
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

data class HASH(private val hashMethod: String) : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(INPUT)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        try {
            val messageDigest = MessageDigest.getInstance(hashMethod)
            if (programContext.stack.size() < 1) {
                throw ProgramException(STACK_UNDERFLOW)
            }
            messageDigest.update(programContext.stack.pop())
            programContext.stack.push(messageDigest.digest())
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalArgumentException("Unknown hash method: $hashMethod", e)
        }
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        throw UnsupportedOperationException()
    }

    override fun toString(): String {
        return "HASH $hashMethod"
    }

    companion object {
        val INPUT = StackParameter("input", 0)
    }
}
