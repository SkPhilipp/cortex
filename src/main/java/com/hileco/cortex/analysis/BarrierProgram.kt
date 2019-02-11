package com.hileco.cortex.analysis

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramBuilder
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import com.hileco.cortex.vm.VirtualMachine.Companion.NUMERICAL_LIMIT
import java.math.BigInteger

data class BarrierProgram(val description: String, val pseudocode: String, val instructions: List<Instruction>) {
    companion object {
        val BARRIER_00 = BarrierProgram("An unconditional win.",
                """HALT(WINNER)""",
                with(ProgramBuilder()) {
                    halt(WINNER)
                    build()
                })
        val BARRIER_01 = BarrierProgram("Basic math on a single input.",
                """ IF(CALL_DATA[1] / 2 == 12345) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(ProgramBuilder()) {
                    blockIf(conditionBody = {
                        equals(divide(push(2), load(CALL_DATA, push(1))), push(12345))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    build()
                })
        val BARRIER_02 = BarrierProgram("Basic math on multiple inputs.",
                """ IF(CALL_DATA[1] / 2 == 12345) {
                  |     IF(CALL_DATA[2] % 500 == 12) {
                  |         HALT(WINNER)
                  |     }
                  | }""".trimMargin(),
                with(ProgramBuilder()) {
                    blockIf(conditionBody = {
                        equals(divide(push(2), load(CALL_DATA, push(1))), push(12345))
                    }, thenBody = {
                        blockIf(conditionBody = {
                            equals(modulo(push(500), load(CALL_DATA, push(2))), push(12))
                        }, thenBody = {
                            halt(WINNER)
                        })
                    })
                    build()
                })
        val BARRIER_03 = BarrierProgram("Vulnerable to integer overflow.",
                """ IF(CALL_DATA[1] + ONE_BELOW_OVERFLOW_LIMIT == 12345) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(ProgramBuilder()) {
                    blockIf(conditionBody = {
                        equals(add(push(NUMERICAL_LIMIT.minus(BigInteger.ONE).toByteArray()), load(CALL_DATA, push(1))), push(12345))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    build()
                })
        val BARRIER_04 = BarrierProgram("Involved memory and loops, more complex data flow analysis.",
                """ VAR x = CALL_DATA[0]
                  | VAR y = 0
                  | WHILE(--x) {
                  |     y++
                  | }
                  | IF(y == 5) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(ProgramBuilder()) {
                    val varX = 2345L
                    val varY = 6789L
                    save(MEMORY, load(CALL_DATA, push(0)), push(varX))
                    save(MEMORY, push(0), push(varY))
                    blockWhile(conditionBody = {
                        save(MEMORY, subtract(push(1), load(MEMORY, push(varX))), push(varX))
                        load(MEMORY, push(varX))
                    }, loopBody = { _, _ ->
                        save(MEMORY, add(push(1), load(MEMORY, push(varY))), push(varY))
                    })
                    blockIf(conditionBody = {
                        equals(push(5), load(MEMORY, push(varY)))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    build()
                })
        val BARRIER_05 = BarrierProgram("Contains somewhat conditional infinite loops.", "".trimIndent(), listOf())
        val BARRIER_06 = BarrierProgram("Requires more complex memory data flow analysis.", "".trimIndent(), listOf())
        val BARRIER_07 = BarrierProgram("This program calls an external program.", "".trimIndent(), listOf())
        val BARRIER_08 = BarrierProgram("Contains a predictable pseudorandom number generator.", "".trimIndent(), listOf())
        val BARRIER_09 = BarrierProgram("Outcome is influenced by preconfigured disk state.", "".trimIndent(), listOf())
        val BARRIER_10 = BarrierProgram("Requires interaction with preconfigured disk state.", "".trimIndent(), listOf())
        val BARRIER_11 = BarrierProgram("Requires multiple calls to solve.", "".trimIndent(), listOf())
    }
}
