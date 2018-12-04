package com.hileco.cortex.documentation.source

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer
import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException.Reason
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
import com.hileco.cortex.vm.ProgramStoreZone.valueOf
import java.io.IOException
import java.math.BigInteger
import java.util.*

class InstructionDeserializer : StdScalarDeserializer<Instruction>(Instruction::class.java) {

    private val stringDeserializer: StringDeserializer = StringDeserializer()

    @FunctionalInterface
    private interface Builder {
        @Throws(IOException::class)
        fun build(split: Array<String>): Instruction
    }

    @Throws(IOException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instruction? {
        val string = this.stringDeserializer.deserialize(p, ctxt) ?: return null
        val split = string.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val type = split[0]
        val builder = MAP[type] ?: throw JsonMappingException(p, String.format("%s is not a known instruction type.", type))
        try {
            return builder.build(split)
        } catch (e: IOException) {
            throw JsonMappingException(p, String.format("Cloud not parse instruction of type %s: %s", type, e.message), e)
        }

    }

    companion object {

        private val MAP: MutableMap<String, Builder>

        private fun require(amount: Int, function: (Array<String>) -> Instruction): Builder {
            return object : Builder {
                override fun build(split: Array<String>): Instruction {
                    val params = split.size - 1
                    if (params != amount) {
                        throw IOException(String.format("Type requires %d parameters, %d given.", amount, params))
                    }
                    return function(split)
                }
            }
        }

        init {
            MAP = HashMap()
            MAP[BITWISE_AND::class.simpleName!!] = require(0) { BITWISE_AND() }
            MAP[BITWISE_NOT::class.simpleName!!] = require(0) { BITWISE_NOT() }
            MAP[BITWISE_OR::class.simpleName!!] = require(0) { BITWISE_OR() }
            MAP[BITWISE_XOR::class.simpleName!!] = require(0) { BITWISE_XOR() }
            MAP[CALL::class.simpleName!!] = require(0) { CALL() }
            MAP[CALL_RETURN::class.simpleName!!] = require(0) { CALL_RETURN() }
            MAP[EQUALS::class.simpleName!!] = require(0) { EQUALS() }
            MAP[GREATER_THAN::class.simpleName!!] = require(0) { GREATER_THAN() }
            MAP[IS_ZERO::class.simpleName!!] = require(0) { IS_ZERO() }
            MAP[LESS_THAN::class.simpleName!!] = require(0) { LESS_THAN() }
            MAP[HALT::class.simpleName!!] = require(1) { parameters -> HALT(Reason.valueOf(parameters[1])) }
            MAP[NOOP::class.simpleName!!] = require(0) { NOOP() }
            MAP[LOAD::class.simpleName!!] = require(1) { parameters -> LOAD(valueOf(parameters[1])) }
            MAP[SAVE::class.simpleName!!] = require(1) { parameters -> SAVE(valueOf(parameters[1])) }
            MAP[EXIT::class.simpleName!!] = require(0) { EXIT() }
            MAP[JUMP::class.simpleName!!] = require(0) { JUMP() }
            MAP[JUMP_DESTINATION::class.simpleName!!] = require(0) { JUMP_DESTINATION() }
            MAP[JUMP_IF::class.simpleName!!] = require(0) { JUMP_IF() }
            MAP[ADD::class.simpleName!!] = require(0) { ADD() }
            MAP[DIVIDE::class.simpleName!!] = require(0) { DIVIDE() }
            MAP[HASH::class.simpleName!!] = require(1) { parameters -> HASH(parameters[1]) }
            MAP[MODULO::class.simpleName!!] = require(0) { MODULO() }
            MAP[MULTIPLY::class.simpleName!!] = require(0) { MULTIPLY() }
            MAP[SUBTRACT::class.simpleName!!] = require(0) { SUBTRACT() }
            MAP[DUPLICATE::class.simpleName!!] = require(1) { parameters -> DUPLICATE(Integer.valueOf(parameters[1])) }
            MAP[POP::class.simpleName!!] = require(0) { POP() }
            MAP[PUSH::class.simpleName!!] = require(1) { parameters -> PUSH(BigInteger(parameters[1]).toByteArray()) }
            MAP[SWAP::class.simpleName!!] = require(2) { parameters -> SWAP(Integer.valueOf(parameters[1]), Integer.valueOf(parameters[2])) }
        }
    }
}
