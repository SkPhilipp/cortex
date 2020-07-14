package com.hileco.cortex.vm.barrier

import com.hileco.cortex.vm.ProgramException.Reason.WINNER
import com.hileco.cortex.vm.ProgramRunner.Companion.OVERFLOW_LIMIT
import com.hileco.cortex.vm.ProgramStoreZone.*
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionsBuilder
import com.hileco.cortex.vm.instructions.stack.ExecutionVariable
import java.math.BigInteger

data class BarrierProgram(val name: String,
                          val description: String,
                          val pseudocode: String,
                          val instructions: List<Instruction>,
                          val diskSetup: Map<BigInteger, BigInteger> = mapOf()) {

    companion object {
        val BARRIER_00 = BarrierProgram("Barrier 00",
                "An unconditional win.",
                """HALT(WINNER)""",
                with(InstructionsBuilder()) {
                    halt(WINNER)
                    build()
                })
        val BARRIER_01 = BarrierProgram("Barrier 01",
                "Basic math on a single input.",
                """ IF(CALL_DATA[1] / 2 == 12345) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    blockIf(conditionBody = {
                        equals(divide(push(2), load(CALL_DATA, push(1))), push(12345))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    build()
                })
        val BARRIER_02 = BarrierProgram("Barrier 02",
                "Basic math on multiple inputs.",
                """ IF(CALL_DATA[1] / 2 == 12345) {
                  |     IF(CALL_DATA[2] % 500 == 12) {
                  |         HALT(WINNER)
                  |     }
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
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
        val BARRIER_03 = BarrierProgram("Barrier 03",
                "Vulnerable to integer overflow.",
                """ IF(CALL_DATA[1] + ONE_BELOW_OVERFLOW_LIMIT == 12345) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    blockIf(conditionBody = {
                        equals(add(push(OVERFLOW_LIMIT.minus(BigInteger.ONE).toByteArray()), load(CALL_DATA, push(1))), push(12345))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    build()
                })
        val BARRIER_04 = BarrierProgram("Barrier 04",
                "Involves memory and loops, more complex data flow analysis.",
                """ VAR x = CALL_DATA[1]
                  | VAR y = 0
                  | WHILE(--x) {
                  |     y++
                  | }
                  | IF(y == 5) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    val varX = 2345L
                    val varY = 6789L
                    save(MEMORY, load(CALL_DATA, push(1)), push(varX))
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
        val BARRIER_05 = BarrierProgram("Barrier 05",
                "Requires more complex memory data flow analysis to solve functions.",
                """ FUNCTION square(N) {
                  |     RETURN N * N
                  | }
                  | FUNCTION cube(N) {
                  |     RETURN N * square(N)
                  | }
                  | IF(cube(CALL_DATA[1]) == 27) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    blockIf(conditionBody = {
                        equals(internalFunctionCall("cube", {
                            load(CALL_DATA, push(1))
                        }), push(27))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    internalFunction("cube", {
                        internalFunctionCall("square", {
                            duplicate(1)
                        })
                        multiply()
                    })
                    internalFunction("square", {
                        duplicate()
                        multiply()
                    })
                    build()
                })
        val BARRIER_06 = BarrierProgram("Barrier 06",
                "Requires constraints on the stack to solve, as to `CALL` the proper address.",
                """ IF(CALL_DATA[1] / 2 == 12345) {
                  |     CALL(RECIPIENT_ADDRESS=CALL_DATA[2], VALUE_TRANSFERRED=1, 0, 0, 0, 0)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    blockIf(conditionBody = {
                        equals(divide(push(2), load(CALL_DATA, push(1))), push(12345))
                    }, thenBody = {
                        call(push(0), push(0), push(0), push(0), push(1), load(CALL_DATA, push(2)), push(0))
                    })
                    build()
                })
        val BARRIER_07 = BarrierProgram("Barrier 07",
                "Contains an infinite loop.",
                """ WHILE(CALL_DATA[1] / 2 == 12345) {
                  |     WHILE(CALL_DATA[2] % 500 == 12) {
                  |     }
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    blockWhile(conditionBody = {
                        equals(divide(push(2), load(CALL_DATA, push(1))), push(12345))
                    }, loopBody = { _, _ ->
                        blockWhile(conditionBody = {
                            equals(modulo(push(500), load(CALL_DATA, push(2))), push(12))
                        }, loopBody = { _, _ ->
                        })
                        halt(WINNER)
                    })
                    build()
                })
        val BARRIER_08 = BarrierProgram("Barrier 08",
                "Represent the mechanics of very primitive PRNGs.",
                """ VAR seed = HASH(ADDRESS_SELF + START_TIME)
                  | if (seed == HASH(CALL_DATA[1])) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    val varSeed = 1000L
                    save(MEMORY, hash("SHA-256", add(variable(ExecutionVariable.ADDRESS_SELF), add(variable(ExecutionVariable.ADDRESS_CALLER), variable(ExecutionVariable.START_TIME)))), push(varSeed))
                    blockIf(conditionBody = {
                        equals(hash("SHA-256", load(CALL_DATA, push(1))), load(MEMORY, push(varSeed)))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    build()
                })
        val BARRIER_09 = BarrierProgram("Barrier 09",
                "Requires understanding of preconfigured `DISK` state.",
                """ if (CALL_DATA[1] == DISK[1]) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    blockIf(conditionBody = {
                        equals(load(CALL_DATA, push(1)), load(DISK, push(1)))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    build()
                },
                mapOf(1.toBigInteger() to 12345.toBigInteger()))
        val BARRIER_10 = BarrierProgram("Barrier 10",
                "Requires multiple calls and understanding of `DISK` state throughout the multiple calls.",
                """ if (CALL_DATA[1] == 1) {
                  |     if (DISK[1] == 12345) {
                  |         HALT(WINNER)
                  |     }
                  | }
                  | if (CALL_DATA[1] == 2) {
                  |     DISK[1] = CALL_DATA[2]
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    blockIf(conditionBody = {
                        equals(load(CALL_DATA, push(1)), push(1))
                    }, thenBody = {
                        blockIf(conditionBody = {
                            equals(load(DISK, push(1)), push(12345))
                        }, thenBody = {
                            halt(WINNER)
                        })
                    })
                    blockIf(conditionBody = {
                        equals(load(CALL_DATA, push(1)), push(2))
                    }, thenBody = {
                        save(DISK, load(CALL_DATA, push(2)), push(1))
                    })
                    build()
                })
        val BARRIER_11 = BarrierProgram("Barrier 11",
                "Matching hashed input with a hashed value.",
                """ VAR hash = HASH(1234)
                  | if (hash == HASH(CALL_DATA[1])) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    val varHash = 1000L
                    save(MEMORY, hash("SHA-256", push(1234)), push(varHash))
                    blockIf(conditionBody = {
                        equals(hash("SHA-256", load(CALL_DATA, push(1))), load(MEMORY, push(varHash)))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    build()
                })
        val BARRIER_12 = BarrierProgram("Barrier 12",
                "Matching hashed input with another hashed input.",
                """ VAR hash = HASH(1234)
                  | if (HASH(CALL_DATA[0]) == HASH(CALL_DATA[1])) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    blockIf(conditionBody = {
                        equals(hash("SHA-256", load(CALL_DATA, push(1))),
                                hash("SHA-256", load(CALL_DATA, push(0))))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    build()
                })
        val BARRIER_13 = BarrierProgram("Barrier 13",
                "Matching input with a hashed value.",
                """ VAR hash = HASH(1234)
                  | if (hash == CALL_DATA[1]) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    val varHash = 1000L
                    save(MEMORY, hash("SHA-256", push(1234)), push(varHash))
                    blockIf(conditionBody = {
                        equals(load(CALL_DATA, push(1)), load(MEMORY, push(varHash)))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    build()
                })
        val BARRIER_14 = BarrierProgram("Barrier 14",
                "Matching hashed input with an known hash.",
                """ VAR hash = 0x....5678
                  | if (hash == HASH(CALL_DATA[1])) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    blockIf(conditionBody = {
                        equals(load(CALL_DATA, push(1)), push(5678))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    build()
                })
        val BARRIER_15 = BarrierProgram("Barrier 15",
                "Solving accidental use of a hash as a condition",
                """ if (HASH(CALL_DATA[0] == CALL_DATA[1])) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    blockIf(conditionBody = {
                        hash("SHA-256", equals(load(CALL_DATA, push(1)), load(CALL_DATA, push(0))))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    build()
                })
        val BARRIER_16 = BarrierProgram("Barrier 16",
                "Comparing input with the hash of another input.",
                """ if (CALL_DATA[0] == HASH(CALL_DATA[1])) {
                  |     HALT(WINNER)
                  | }""".trimMargin(),
                with(InstructionsBuilder()) {
                    blockIf(conditionBody = {
                        equals(load(CALL_DATA, push(0)), hash("SHA-256", load(CALL_DATA, push(1))))
                    }, thenBody = {
                        halt(WINNER)
                    })
                    build()
                })

        val BARRIERS = listOf(
                BARRIER_00,
                BARRIER_01,
                BARRIER_02,
                BARRIER_03,
                BARRIER_04,
                BARRIER_05,
                BARRIER_06,
                BARRIER_07,
                BARRIER_08,
                BARRIER_09,
                BARRIER_11,
                BARRIER_12,
                BARRIER_13,
                BARRIER_14,
                BARRIER_15,
                BARRIER_16)
    }
}
