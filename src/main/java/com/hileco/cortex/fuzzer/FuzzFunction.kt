package com.hileco.cortex.fuzzer

import com.hileco.cortex.instructions.calls.CALL
import com.hileco.cortex.instructions.calls.CALL_RETURN
import com.hileco.cortex.instructions.io.SAVE
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.instructions.stack.SWAP
import com.hileco.cortex.vm.ProgramStoreZone
import java.math.BigInteger
import java.util.function.Consumer

enum class FuzzFunction(
        private val chance: Double,
        private val implementation: (ProgramGeneratorContext) -> Unit
) : Chanced, Consumer<ProgramGeneratorContext> {

    EXIT_ONLY(0.3, { context -> context.currentBuilder().include { EXIT() } }),

    RETURN_ONLY(3.0, { context -> context.currentBuilder().include { CALL_RETURN() } }),

    CALL_WITH_FUNDS(1.0, { context ->
        with(context.currentBuilder()) {
            include { PUSH(context.random().toByteArray()) }
            include { PUSH(context.random().toByteArray()) }
            include { SWAP(0, context.randomIntBetween(1, STACK_SWAP_UPPER_BOUND)) }
            include { CALL() }
        }
    }),

    CALL_LIBRARY(1.0, { context ->
        with(context.currentBuilder()) {
            val choices = context.atlas().keySet()
            if (!choices.isEmpty()) {
                val address = choices.toTypedArray()[context.randomIntBetween(0, choices.size)]
                include { PUSH(BigInteger.ZERO.toByteArray()) }
                include { PUSH(address.toByteArray()) }
                include { CALL() }
            } else {
                include { EXIT() }
            }
        }
    }),

    SAVE_ONLY(3.0, { context -> context.currentBuilder().include { SAVE(ProgramStoreZone.DISK) } });

    override fun chance(): Double {
        return this.chance
    }

    override fun accept(programGeneratorContext: ProgramGeneratorContext) {
        implementation(programGeneratorContext)
    }

    companion object Constants {
        const val STACK_SWAP_UPPER_BOUND = 10
    }
}