package com.hileco.cortex.instructions

import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.stack.PUSH
import java.math.BigInteger
import java.util.*

class InstructionsBuilder {
    private val instructions: MutableList<() -> Instruction> = ArrayList()
    private val labelAddresses: MutableMap<String, Int> = HashMap()

    fun include(supplier: () -> Instruction) {
        instructions.add(supplier)
    }

    fun pushLabel(name: String) {
        include {
            val address = labelAddresses[name]
            if (address == null) {
                throw IllegalStateException("No label for name $name")
            } else {
                PUSH(BigInteger.valueOf(address.toLong()).toByteArray())
            }
        }
    }

    fun markLabel(name: String) {
        if (labelAddresses.containsKey(name)) {
            throw IllegalArgumentException("Name $name is already taken")
        }
        labelAddresses[name] = instructions.size
        include { JUMP_DESTINATION() }
    }

    fun size(): Int {
        return instructions.size
    }

    fun build(): List<Instruction> {
        return instructions.map { it() }
    }
}
