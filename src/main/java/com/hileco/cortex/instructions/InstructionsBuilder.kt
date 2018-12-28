package com.hileco.cortex.instructions

import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.stack.PUSH
import java.math.BigInteger
import java.util.*
import java.util.stream.Collectors

class InstructionsBuilder {
    private val instructions: MutableList<() -> Instruction> = ArrayList()
    private val labelAddresses: MutableMap<String, Int> = HashMap()

    fun include(supplier: () -> Instruction) {
        instructions.add(supplier)
    }

    fun PUSH_LABEL(name: String) {
        include {
            val address = labelAddresses[name]
            if (address == null) {
                throw IllegalStateException("No label for name $name")
            } else {
                PUSH(BigInteger.valueOf(address.toLong()).toByteArray())
            }
        }
    }

    fun MARK_LABEL(name: String) {
        if (labelAddresses.containsKey(name)) {
            throw IllegalArgumentException("Name $name is already taken")
        }
        labelAddresses[name] = instructions.size
        include { JUMP_DESTINATION() }
    }

    fun LOOP(loopBody: (InstructionsBuilder) -> Unit) {
        val startLabel = UUID.randomUUID().toString()
        MARK_LABEL(startLabel)
        loopBody(this)
        PUSH_LABEL(startLabel)
        include { JUMP() }
    }

    fun LOOP(conditionBody: (InstructionsBuilder) -> Unit, loopBody: (InstructionsBuilder) -> Unit) {
        val startLabel = UUID.randomUUID().toString()
        val endLabel = UUID.randomUUID().toString()
        MARK_LABEL(startLabel)
        conditionBody(this)
        include { IS_ZERO() }
        PUSH_LABEL(endLabel)
        include { JUMP_IF() }
        loopBody(this)
        PUSH_LABEL(startLabel)
        include { JUMP() }
        MARK_LABEL(endLabel)
    }

    fun IF(condition: (InstructionsBuilder) -> Unit, content: (InstructionsBuilder) -> Unit) {
        val endLabel = UUID.randomUUID().toString()
        condition(this)
        include { IS_ZERO() }
        PUSH_LABEL(endLabel)
        include { JUMP_IF() }
        content(this)
        MARK_LABEL(endLabel)
    }

    fun size(): Int {
        return instructions.size
    }

    fun build(): List<Instruction> {
        return instructions.stream()
                .map { it() }
                .collect(Collectors.toList())
    }

    fun include(others: List<Instruction>) {
        others.forEach { instruction -> instructions.add { instruction } }
    }
}
