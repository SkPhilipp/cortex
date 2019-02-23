package com.hileco.cortex.analysis.attack

import com.hileco.cortex.constraints.Solution
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.calls.CALL
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.io.SAVE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.concrete.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.concrete.ProgramStoreZone.MEMORY

class AttackProgramBuilder {
    fun build(targetAddress: Long, solution: Solution): List<Instruction> {
        if (!solution.isSolvable) {
            throw IllegalArgumentException("Only solveable solutions are supported.")
        }
        val instructions = ArrayList<Instruction>()
        var callDataEnd = 0L
        solution.possibleValues.forEach { reference, suggestedValue ->
            if (reference.type == CALL_DATA && reference.address is Expression.Value) {
                instructions.add(PUSH(suggestedValue))
                instructions.add(PUSH(reference.address.constant))
                instructions.add(SAVE(MEMORY))
                callDataEnd = Math.max(reference.address.constant + 1, callDataEnd)
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
        instructions.add(CALL())
        return instructions
    }
}