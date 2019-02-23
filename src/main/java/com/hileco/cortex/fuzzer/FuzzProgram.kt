package com.hileco.cortex.fuzzer

import com.hileco.cortex.vm.concrete.ProgramStoreZone.CALL_DATA
import java.util.*

enum class FuzzProgram(private val chance: Double,
                       val implementation: (ProgramGeneratorContext) -> Unit) : Chanced {
    /**
     * A commonly used program layout; A jump table for each function of the program.
     */
    FUNCTION_TABLE(12.0, { context ->
        val functionsTable = ArrayList<Long>()
        context.forRandom(1, LIMIT_INITIAL_FUNCTIONS) {
            functionsTable.add(context.random().toLong())
        }
        context.builder.blockSwitch(controlBody = {
            context.forRandom(1, LIMIT_INITIAL_CALL_DATA_LOAD) {
                context.builder.push(context.randomBetween(0, LIMIT_SIZE_CALL_DATA).toByteArray())
                context.builder.load(CALL_DATA)
            }
        }, cases = functionsTable, caseBuilder = { context.randomFuzzFunction() })
    }),

    FUNCTION(1.0, { context ->
        context.forRandom(1, LIMIT_INITIAL_CALL_DATA_LOAD) {
            context.builder.push(context.randomBetween(0, LIMIT_SIZE_CALL_DATA).toByteArray())
            context.builder.load(CALL_DATA)
        }
        context.randomFuzzFunction()
    });

    override fun chance(): Double {
        return this.chance
    }

    companion object {
        const val LIMIT_INITIAL_FUNCTIONS = 10
        const val LIMIT_INITIAL_CALL_DATA_LOAD = 10
        const val LIMIT_SIZE_CALL_DATA = 8192
    }
}
