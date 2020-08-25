package com.hileco.cortex.analysis.attack

import com.hileco.cortex.symbolic.Solution
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ONE_32
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.calls.CALL
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.io.SAVE
import com.hileco.cortex.vm.instructions.stack.PUSH

class AttackProgramBuilder {
    fun build(targetAddress: BackedInteger, solution: Solution): List<Instruction> {
        if (!solution.solvable) {
            throw IllegalArgumentException("Only solveable solutions are supported.")
        }
        val instructions = ArrayList<Instruction>()
        var callDataEnd = ZERO_32
        solution.values.forEach { (reference, suggestedValue) ->
            if (reference.type == CALL_DATA && reference.address is Expression.Value) {
                val address = reference.address as Expression.Value
                instructions.add(PUSH(suggestedValue))
                instructions.add(PUSH(address.constant))
                instructions.add(SAVE(MEMORY))
                callDataEnd = BackedInteger.max(address.constant + ONE_32, callDataEnd)
            } else {
                throw IllegalArgumentException("Only known-address CALL_DATA-based solutions are are supported.")
            }
        }
        instructions.add(PUSH(ZERO_32))
        instructions.add(PUSH(ZERO_32))
        instructions.add(PUSH(callDataEnd * LOAD.SIZE.toBackedInteger()))
        instructions.add(PUSH(ZERO_32))
        instructions.add(PUSH(ZERO_32))
        instructions.add(PUSH(targetAddress))
        instructions.add(PUSH(ZERO_32))
        instructions.add(CALL())
        return instructions
    }
}