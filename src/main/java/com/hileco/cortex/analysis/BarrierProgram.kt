package com.hileco.cortex.analysis

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.InstructionsBuilder
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.io.SAVE
import com.hileco.cortex.instructions.math.ADD
import com.hileco.cortex.instructions.math.DIVIDE
import com.hileco.cortex.instructions.math.MODULO
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.VirtualMachine
import java.math.BigInteger

data class BarrierProgram(val description: String, val pseudocode: String, val instructions: List<Instruction>) {
    companion object {
        val BARRIER_00 = BarrierProgram("Unconditional Win",
                """HALT(WINNER)""",
                InstructionsBuilder().let { builder ->
                    builder.include { HALT(ProgramException.Reason.WINNER) }
                    builder.build()
                })
        val BARRIER_01 = BarrierProgram("Basic math.",
                """ IF(CALL_DATA[1] / 2 == 12345) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                InstructionsBuilder().let { builder ->
                    builder.includeIf(conditionBody = {
                        builder.include { PUSH(2) }
                        builder.include { PUSH(1) }
                        builder.include { LOAD(ProgramStoreZone.CALL_DATA) }
                        builder.include { DIVIDE() }
                        builder.include { PUSH(12345) }
                        builder.include { EQUALS() }
                    }, blockBody = {
                        builder.include { HALT(ProgramException.Reason.WINNER) }
                    })
                    builder.build()
                })
        val BARRIER_02 = BarrierProgram("Basic math, with two inputs.",
                """ IF(CALL_DATA[1] / 2 == 12345) {
                  |     IF(CALL_DATA[2] % 500 == 12) {
                  |         HALT(WINNER)
                  |     }
                  | }""".trimMargin(),
                InstructionsBuilder().let { builder ->
                    builder.includeIf(conditionBody = {
                        builder.include { PUSH(2) }
                        builder.include { PUSH(1) }
                        builder.include { LOAD(ProgramStoreZone.CALL_DATA) }
                        builder.include { DIVIDE() }
                        builder.include { PUSH(12345) }
                        builder.include { EQUALS() }
                    }, blockBody = {
                        builder.includeIf(conditionBody = {
                            builder.include { PUSH(500) }
                            builder.include { PUSH(2) }
                            builder.include { LOAD(ProgramStoreZone.CALL_DATA) }
                            builder.include { MODULO() }
                            builder.include { PUSH(12) }
                            builder.include { EQUALS() }
                        }, blockBody = {
                            builder.include { HALT(ProgramException.Reason.WINNER) }
                        })
                    })
                    builder.build()
                })
        val BARRIER_03 = BarrierProgram("Exploiting integer overflow.",
                """ IF(CALL_DATA[1] + ONE_BELOW_OVERFLOW_LIMIT == 12345) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                InstructionsBuilder().let { builder ->
                    builder.includeIf(conditionBody = {
                        builder.include { PUSH(VirtualMachine.NUMERICAL_LIMIT.minus(BigInteger.ONE).toByteArray()) }
                        builder.include { PUSH(1) }
                        builder.include { LOAD(ProgramStoreZone.CALL_DATA) }
                        builder.include { ADD() }
                        builder.include { PUSH(12345) }
                        builder.include { EQUALS() }
                    }, blockBody = {
                        builder.include { HALT(ProgramException.Reason.WINNER) }
                    })
                    builder.build()
                })
        val BARRIER_04 = BarrierProgram("Loops & memory.",
                """ x = CALL_DATA[0]
                  | y = 0
                  | WHILE(--x != 0) {
                  |     y++
                  | }
                  | IF(y == 5) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                InstructionsBuilder().let { builder ->
                    builder.include { PUSH(0) }
                    builder.include { LOAD(ProgramStoreZone.CALL_DATA) }
                    builder.include { PUSH(0) }
                    builder.include { SAVE(ProgramStoreZone.MEMORY) }
                    builder.include { PUSH(0) }
                    builder.include { PUSH(1000) }
                    builder.include { SAVE(ProgramStoreZone.MEMORY) }
                    builder.includeLoop(conditionBody = {
                        builder.decrement(ProgramStoreZone.MEMORY, BigInteger.valueOf(0).toByteArray())
                        builder.include { PUSH(0) }
                        builder.include { LOAD(ProgramStoreZone.MEMORY) }
                        builder.include { PUSH(0) }
                        builder.include { EQUALS() }
                        builder.include { IS_ZERO() }
                    }, loopBody = {
                        builder.increment(ProgramStoreZone.MEMORY, BigInteger.valueOf(1000).toByteArray())
                    })
                    builder.includeIf(conditionBody = {
                        builder.include { PUSH(1000) }
                        builder.include { LOAD(ProgramStoreZone.MEMORY) }
                        builder.include { PUSH(5) }
                        builder.include { EQUALS() }
                    }, blockBody = {
                        builder.include { HALT(ProgramException.Reason.WINNER) }
                    })
                    builder.build()
                }
        )
    }
}
