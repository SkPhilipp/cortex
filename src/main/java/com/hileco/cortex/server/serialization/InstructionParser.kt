package com.hileco.cortex.server.serialization

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.bits.BITWISE_AND
import com.hileco.cortex.instructions.bits.BITWISE_NOT
import com.hileco.cortex.instructions.bits.BITWISE_OR
import com.hileco.cortex.instructions.bits.BITWISE_XOR
import com.hileco.cortex.instructions.calls.CALL
import com.hileco.cortex.instructions.calls.CALL_RETURN
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.conditions.GREATER_THAN
import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.conditions.LESS_THAN
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.io.SAVE
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.math.*
import com.hileco.cortex.instructions.stack.DUPLICATE
import com.hileco.cortex.instructions.stack.POP
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.instructions.stack.SWAP
import com.hileco.cortex.vm.concrete.ProgramStoreZone
import java.io.IOException
import java.math.BigInteger

class InstructionParser {
    @Throws(IOException::class)
    fun parse(string: String): Instruction {
        val split = string.replace("(.*?)\\s*--.*".toRegex(), "$1")
                .split("\\s+".toRegex())
                .toTypedArray()
        val type = split.first()
        val builder = MAP[type] ?: throw IOException("$type is not a known instruction type.")
        return builder(split)
    }

    companion object {
        private fun require(amount: Int, function: (Array<String>) -> Instruction): (Array<String>) -> Instruction {
            return { split ->
                val params = split.size - 1
                if (params != amount) {
                    throw IOException("Type requires $amount parameters, $params given.")
                }
                function(split)
            }
        }

        private val MAP: Map<String, (Array<String>) -> Instruction> = mapOf(
                "BITWISE_AND" to require(0) { BITWISE_AND() },
                "BITWISE_NOT" to require(0) { BITWISE_NOT() },
                "BITWISE_OR" to require(0) { BITWISE_OR() },
                "BITWISE_XOR" to require(0) { BITWISE_XOR() },
                "CALL" to require(0) { CALL() },
                "CALL_RETURN" to require(0) { CALL_RETURN() },
                "EQUALS" to require(0) { EQUALS() },
                "GREATER_THAN" to require(0) { GREATER_THAN() },
                "IS_ZERO" to require(0) { IS_ZERO() },
                "LESS_THAN" to require(0) { LESS_THAN() },
                "HALT" to require(1) { parameters -> HALT(ProgramException.Reason.valueOf(parameters[1])) },
                "NOOP" to require(0) { NOOP() },
                "" to require(0) { NOOP() },
                "LOAD" to require(1) { parameters -> LOAD(ProgramStoreZone.valueOf(parameters[1])) },
                "SAVE" to require(1) { parameters -> SAVE(ProgramStoreZone.valueOf(parameters[1])) },
                "EXIT" to require(0) { EXIT() },
                "JUMP" to require(0) { JUMP() },
                "JUMP_DESTINATION" to require(0) { JUMP_DESTINATION() },
                "JUMP_IF" to require(0) { JUMP_IF() },
                "ADD" to require(0) { ADD() },
                "DIVIDE" to require(0) { DIVIDE() },
                "HASH" to require(1) { parameters -> HASH(parameters[1]) },
                "MODULO" to require(0) { MODULO() },
                "MULTIPLY" to require(0) { MULTIPLY() },
                "SUBTRACT" to require(0) { SUBTRACT() },
                "DUPLICATE" to require(1) { parameters -> DUPLICATE(Integer.valueOf(parameters[1])) },
                "POP" to require(0) { POP() },
                "PUSH" to require(1) { parameters -> PUSH(BigInteger(parameters[1]).toByteArray()) },
                "SWAP" to require(2) { parameters -> SWAP(Integer.valueOf(parameters[1]), Integer.valueOf(parameters[2])) }
        )
    }
}
