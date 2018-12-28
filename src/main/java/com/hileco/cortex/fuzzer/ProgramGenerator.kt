package com.hileco.cortex.fuzzer

import com.hileco.cortex.instructions.InstructionsBuilder
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.layer.LayeredMap
import java.math.BigInteger

class ProgramGenerator {
    fun generate(seed: Long): LayeredMap<BigInteger, Program> {
        val context = ProgramGeneratorContext(seed)
        context.forRandom(1, LIMIT_INITIAL_PROGRAMS) {
            context.pushBuilder(InstructionsBuilder())
            context.randomFuzzProgram()(context)
            val address = context.random()
            val generated = Program(context.currentBuilder().build(), address)
            context.atlas()[address] = generated
            context.popBuilder()
        }
        return context.atlas()
    }

    companion object {
        private const val LIMIT_INITIAL_PROGRAMS = 10
    }
}
