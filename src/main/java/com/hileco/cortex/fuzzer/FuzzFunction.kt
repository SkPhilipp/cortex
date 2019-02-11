package com.hileco.cortex.fuzzer

import com.hileco.cortex.vm.ProgramStoreZone

enum class FuzzFunction(private val chance: Double,
                        private val implementation: (ProgramGeneratorContext) -> Unit) : Chanced, (ProgramGeneratorContext) -> Unit {
    EXIT_ONLY(0.3, { context -> context.builder.exit() }),

    RETURN_ONLY(3.0, { context -> context.builder.callReturn() }),

    CALL_WITH_FUNDS(1.0, { context ->
        with(context.builder) {
            push(context.random().toByteArray())
            push(context.random().toByteArray())
            swap(0, context.randomIntBetween(1, STACK_SWAP_UPPER_BOUND))
            call()
        }
    }),

    CALL_LIBRARY(1.0, { context ->
        with(context.builder) {
            val choices = context.atlas().keySet()
            if (!choices.isEmpty()) {
                val address = choices.toTypedArray()[context.randomIntBetween(0, choices.size)]
                push(0)
                push(address.toByteArray())
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

    override fun invoke(programGeneratorContext: ProgramGeneratorContext) {
        implementation(programGeneratorContext)
    }

    companion object {
        const val STACK_SWAP_UPPER_BOUND = 10
    }
}
