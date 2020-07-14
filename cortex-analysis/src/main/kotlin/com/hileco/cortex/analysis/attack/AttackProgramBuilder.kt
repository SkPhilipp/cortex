package com.hileco.cortex.analysis.attack

import com.hileco.cortex.symbolic.Solution
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.calls.CALL
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.io.SAVE
import com.hileco.cortex.vm.instructions.stack.PUSH

class AttackProgramBuilder {
    fun build(targetAddress: Long, solution: Solution): List<Instruction> {
        if (!solution.solvable) {
            throw IllegalArgumentException("Only solveable solutions are supported.")
        }
        val instructions = ArrayList<Instruction>()
        var callDataEnd = 0L
        solution.values.forEach { reference, suggestedValue ->
            if (reference.type == CALL_DATA && reference.address is Expression.Value) {
                val address = reference.address as Expression.Value
                instructions.add(PUSH(suggestedValue))
                instructions.add(PUSH(address.constant))
                instructions.add(SAVE(MEMORY))
                callDataEnd = Math.max(address.constant + 1, callDataEnd)
            } else {
                throw IllegalArgumentException("Only known-address CALL_DATA-based solutions are are supported.")
            }
        }
        instructions.add(PUSH(0))
        instructions.add(PUSH(0))
        instructions.add(PUSH(callDataEnd * LOAD.SIZE))
        instructions.add(PUSH(0))
        instructions.add(PUSH(0))
        instructions.add(PUSH(targetAddress))
        instructions.add(PUSH(0))
        instructions.add(CALL())
        return instructions
    }
}