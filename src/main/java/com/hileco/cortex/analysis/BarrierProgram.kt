package com.hileco.cortex.analysis

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.InstructionsBuilder
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.math.DIVIDE
import com.hileco.cortex.instructions.math.MODULO
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone

data class BarrierProgram(val description: String, val pseudocode: String, val instructions: List<Instruction>) {
    companion object {
        val BARRIER_01 = BarrierProgram("Basic math.",
                """ IF(CALL_DATA[1] / 2 == 12345) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                InstructionsBuilder().let {
                    it.includeIf({ condition ->
                        condition.include { PUSH(2) }
                        condition.include { PUSH(1) }
                        condition.include { LOAD(ProgramStoreZone.CALL_DATA) }
                        condition.include { DIVIDE() }
                        condition.include { PUSH(12345) }
                        condition.include { EQUALS() }
                    }, { block ->
                        block.include { HALT(ProgramException.Reason.WINNER) }
                    })
                    it.build()
                })
        val BARRIER_02 = BarrierProgram("Basic math, with two inputs.",
                """ IF(CALL_DATA[1] / 2 == 12345) {
                  |     IF(CALL_DATA[2] % 500 == 12) {
                  |         HALT(WINNER)
                  |     }
                  | }""".trimMargin(),
                InstructionsBuilder().let {
                    it.includeIf({ outerCondition ->
                        outerCondition.include { PUSH(2) }
                        outerCondition.include { PUSH(1) }
                        outerCondition.include { LOAD(ProgramStoreZone.CALL_DATA) }
                        outerCondition.include { DIVIDE() }
                        outerCondition.include { PUSH(12345) }
                        outerCondition.include { EQUALS() }
                    }, { outerBlock ->
                        outerBlock.includeIf({ innerCondition ->
                            innerCondition.include { PUSH(500) }
                            innerCondition.include { PUSH(2) }
                            innerCondition.include { LOAD(ProgramStoreZone.CALL_DATA) }
                            innerCondition.include { MODULO() }
                            innerCondition.include { PUSH(12) }
                            innerCondition.include { EQUALS() }
                        }, { innerBlock ->
                            innerBlock.include { HALT(ProgramException.Reason.WINNER) }
                        })
                    })
                    it.build()
                })
    }
}
