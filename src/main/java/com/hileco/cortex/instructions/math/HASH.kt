package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression
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
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

data class HASH(private val method: String) : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(INPUT)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        try {
            val messageDigest = MessageDigest.getInstance(method)
            if (programContext.stack.size() < 1) {
                throw ProgramException(STACK_UNDERFLOW)
            }
            // TODO: This conversion is currently needed due to BigInteger-sourced values not yet being padded with 0
            val byteArray = BigInteger(programContext.stack.pop()).toByteArray()
            messageDigest.update(byteArray)
            programContext.stack.push(messageDigest.digest())
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalArgumentException("Unknown hash method: $method", e)
        }
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        try {
            if (programContext.stack.size() < 1) {
                throw ProgramException(STACK_UNDERFLOW)
            }
            val result = innerExecute(programContext.stack.pop())
            programContext.stack.push(result)
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalArgumentException("Unknown hash method: $method", e)
        }
    }

    fun innerExecute(expression: Expression): Expression.Hash {
        return Expression.Hash(expression, method)
    }

    override fun toString(): String {
        return "HASH $method"
    }

    companion object {
        val INPUT = StackParameter("input", 0)
    }
}
