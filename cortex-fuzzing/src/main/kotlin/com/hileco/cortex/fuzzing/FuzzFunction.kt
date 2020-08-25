package com.hileco.cortex.fuzzing

import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32

enum class FuzzFunction(private val chance: Double,
                        val implementation: (ProgramGeneratorContext) -> Unit) : Chanced {
    EXIT_ONLY(0.3, { context -> context.builder.exit() }),

    RETURN_ONLY(3.0, { context -> context.builder.callReturn() }),

    CALL_WITH_FUNDS(1.0, { context ->
        with(context.builder) {
            push(context.random())
            push(context.random())
            swap(0, context.randomIntBetween(1, STACK_SWAP_UPPER_BOUND))
            call()
        }
    }),

    CALL_LIBRARY(1.0, { context ->
        with(context.builder) {
            val choices = context.atlas().keySet()
            if (choices.isNotEmpty()) {
                val address = choices.toTypedArray()[context.randomIntBetween(0, choices.size)]
                push(ZERO_32)
                push(address)
                call()
            } else {
                exit()
            }
        }
    }),

    SAVE_ONLY(3.0, { context -> context.builder.save(ProgramStoreZone.DISK) });

    override fun chance(): Double {
        return this.chance
    }

    companion object {
        const val STACK_SWAP_UPPER_BOUND = 10
    }
}
