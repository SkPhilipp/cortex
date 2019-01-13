package com.hileco.cortex.instructions

import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.io.SAVE
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.math.ADD
import com.hileco.cortex.instructions.math.SUBTRACT
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone
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

    fun increment(programStoreZone: ProgramStoreZone, address: ByteArray) {
        include { PUSH(address) }
        include { LOAD(programStoreZone) }
        include { PUSH(1) }
        include { ADD() }
        include { PUSH(address) }
        include { SAVE(programStoreZone) }
    }

    fun decrement(programStoreZone: ProgramStoreZone, address: ByteArray) {
        include { PUSH(address) }
        include { LOAD(programStoreZone) }
        include { PUSH(1) }
        include { SUBTRACT() }
        include { PUSH(address) }
        include { SAVE(programStoreZone) }
    }

    fun includeLoop(loopBody: (InstructionsBuilder) -> Unit) {
        val startLabel = UUID.randomUUID().toString()
        markLabel(startLabel)
        loopBody(this)
        pushLabel(startLabel)
        include { JUMP() }
    }

    fun includeLoop(conditionBody: (InstructionsBuilder) -> Unit, loopBody: (InstructionsBuilder) -> Unit) {
        val startLabel = UUID.randomUUID().toString()
        val endLabel = UUID.randomUUID().toString()
        markLabel(startLabel)
        conditionBody(this)
        include { IS_ZERO() }
        pushLabel(endLabel)
        include { JUMP_IF() }
        loopBody(this)
        pushLabel(startLabel)
        include { JUMP() }
        markLabel(endLabel)
    }

    fun includeIf(conditionBody: (InstructionsBuilder) -> Unit, blockBody: (InstructionsBuilder) -> Unit) {
        val endLabel = UUID.randomUUID().toString()
        conditionBody(this)
        include { IS_ZERO() }
        pushLabel(endLabel)
        include { JUMP_IF() }
        blockBody(this)
        markLabel(endLabel)
    }

    fun size(): Int {
        return instructions.size
    }

    fun build(): List<Instruction> {
        return instructions.map { it() }
    }
}
