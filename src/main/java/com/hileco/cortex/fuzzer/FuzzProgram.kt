package com.hileco.cortex.fuzzer

import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone
import java.math.BigInteger
import java.util.*
import java.util.function.Consumer

enum class FuzzProgram(
        private val chance: Double,
        private val implementation: (ProgramGeneratorContext) -> Unit
) : Chanced, Consumer<ProgramGeneratorContext> {

    /**
     * A commonly used program layout; A jump table for each function of the program.
     */
    FUNCTION_TABLE(12.0, { context ->
        context.forRandom(1, LIMIT_INITIAL_CALL_DATA_LOAD) {
            context.currentBuilder().include { PUSH(context.randomBetween(0, LIMIT_SIZE_CALL_DATA).toByteArray()) }
            context.currentBuilder().include { LOAD(ProgramStoreZone.CALL_DATA) }
        }
        val functions = ArrayList<BigInteger>()
        context.forRandom(1, LIMIT_INITIAL_FUNCTIONS) { functions.add(context.random()) }
        functions.forEach { address ->
            context.currentBuilder().include { PUSH(context.random().toByteArray()) }
            context.currentBuilder().include { EQUALS() }
            context.currentBuilder().PUSH_LABEL(address.toString())
            context.currentBuilder().include { JUMP_IF() }
        }
        context.currentBuilder().PUSH_LABEL(PROGRAM_END_LABEL)
        context.currentBuilder().include { JUMP() }
        functions.forEach { address ->
            context.currentBuilder().MARK_LABEL(address.toString())
            context.randomFuzzFunction().accept(context)
            context.currentBuilder().PUSH_LABEL(PROGRAM_END_LABEL)
            context.currentBuilder().include { JUMP() }
        }
        context.currentBuilder().MARK_LABEL(PROGRAM_END_LABEL)
    }),

    FUNCTION(1.0, { context ->
        context.forRandom(1, LIMIT_INITIAL_CALL_DATA_LOAD) {
            context.currentBuilder().include { PUSH(context.randomBetween(0, LIMIT_SIZE_CALL_DATA).toByteArray()) }
            context.currentBuilder().include { LOAD(ProgramStoreZone.CALL_DATA) }
        }
        context.randomFuzzFunction().accept(context)
    });

    override fun chance(): Double {
        return this.chance
    }

    override fun accept(programGeneratorContext: ProgramGeneratorContext) {
        implementation(programGeneratorContext)
    }

    companion object {
        const val LIMIT_INITIAL_FUNCTIONS = 10
        const val LIMIT_INITIAL_CALL_DATA_LOAD = 10
        const val LIMIT_SIZE_CALL_DATA = 8192
        const val PROGRAM_END_LABEL = "end"
    }
}