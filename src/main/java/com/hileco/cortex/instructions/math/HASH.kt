package com.hileco.cortex.instructions.math

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.VirtualMachine
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

data class HASH(private val hashMethod: String) : Instruction() {

    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(INPUT)

    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        try {
            val messageDigest = MessageDigest.getInstance(hashMethod)
            if (program.stack.size() < 1) {
                throw ProgramException(program, STACK_TOO_FEW_ELEMENTS)
            }
            messageDigest.update(program.stack.pop()!!)
            program.stack.push(messageDigest.digest())
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalArgumentException(String.format("Unknown hash method: %s", hashMethod), e)
        }

    }

    override fun toString(): String {
        return String.format("HASH %s", hashMethod)
    }

    companion object {
        val INPUT = StackParameter("input", 0)
    }
}
