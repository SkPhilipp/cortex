package com.hileco.cortex.fuzzer

import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone
import java.math.BigInteger
import java.util.*

enum class FuzzProgram(private val chance: Double,
                       private val implementation: (ProgramGeneratorContext) -> Unit) : Chanced, (ProgramGeneratorContext) -> Unit {
    /**
     * A commonly used program layout; A jump table for each function of the program.
     */
    FUNCTION_TABLE(12.0, { context ->
        context.forRandom(1, LIMIT_INITIAL_CALL_DATA_LOAD) {
            context.builder.include { PUSH(context.randomBetween(0, LIMIT_SIZE_CALL_DATA).toByteArray()) }
            context.builder.include { LOAD(ProgramStoreZone.CALL_DATA) }
        }
        val functions = ArrayList<BigInteger>()
        context.forRandom(1, LIMIT_INITIAL_FUNCTIONS) { functions.add(context.random()) }
        functions.forEach { address ->
            context.builder.include { PUSH(context.random().toByteArray()) }
            context.builder.include { EQUALS() }
            context.builder.pushLabel("$address")
            context.builder.include { JUMP_IF() }
        }
        context.builder.pushLabel(PROGRAM_END_LABEL)
        context.builder.include { JUMP() }
        functions.forEach { address ->
            context.builder.markLabel("$address")
            context.randomFuzzFunction()(context)
            context.builder.pushLabel(PROGRAM_END_LABEL)
            context.builder.include { JUMP() }
        }
        context.builder.markLabel(PROGRAM_END_LABEL)
    }),

    FUNCTION(1.0, { context ->
        context.forRandom(1, LIMIT_INITIAL_CALL_DATA_LOAD) {
            context.builder.include { PUSH(context.randomBetween(0, LIMIT_SIZE_CALL_DATA).toByteArray()) }
            context.builder.include { LOAD(ProgramStoreZone.CALL_DATA) }
        }
        context.randomFuzzFunction()(context)
    });

    override fun chance(): Double {
        return this.chance
    }

    override fun invoke(programGeneratorContext: ProgramGeneratorContext) {
        implementation(programGeneratorContext)
    }

    companion object {
        const val LIMIT_INITIAL_FUNCTIONS = 10
        const val LIMIT_INITIAL_CALL_DATA_LOAD = 10
        const val LIMIT_SIZE_CALL_DATA = 8192
        const val PROGRAM_END_LABEL = "end"
    }
}
