package com.hileco.cortex.analysis.attack

import com.hileco.cortex.symbolic.Solution
import com.hileco.cortex.symbolic.expressions.Expression.Variable
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.calls.CALL
import com.hileco.cortex.vm.instructions.io.SAVE
import com.hileco.cortex.vm.instructions.stack.PUSH

class AttackProgramBuilder {
    fun build(targetAddress: BackedInteger, solution: Solution): List<Instruction> {
        if (!solution.solvable) {
            throw IllegalArgumentException("Only solveable solutions are supported.")
        }
        val instructions = ArrayList<Instruction>()
        val callData = solution.values.asSequence()
                .filter { (variable, _) -> variable is Variable && variable.name == CALL_DATA.name }
                .map { (_, suggestedValue) -> suggestedValue }
                .firstOrNull() ?: ByteArray(0)

        val limit = (callData.size + 31) / 32
        for (i in 0..limit) {
            val offset = i * 32
            instructions.add(PUSH(BackedInteger(callData.slice(IntRange(offset, (offset + 31).coerceAtMost(callData.size - 1))))))
            instructions.add(PUSH(offset.toBackedInteger()))
            instructions.add(SAVE(MEMORY))
        }
        instructions.add(PUSH(ZERO_32))
        instructions.add(PUSH(ZERO_32))
        instructions.add(PUSH((callData.size * 32).toBackedInteger()))
        instructions.add(PUSH(ZERO_32))
        instructions.add(PUSH(ZERO_32))
        instructions.add(PUSH(targetAddress))
        instructions.add(PUSH(ZERO_32))
        instructions.add(CALL())
        return instructions
    }
}