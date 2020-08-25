package com.hileco.cortex.fuzzing

import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.instructions.InstructionsBuilder

class ProgramGenerator {
    fun generate(seed: Long): LayeredVmMap<BackedInteger, Program> {
        val context = ProgramGeneratorContext(seed)
        context.forRandom(1, LIMIT_INITIAL_PROGRAMS) {
            context.builder = InstructionsBuilder()
            context.randomFuzzProgram()
            val address = context.random()
            val generated = Program(context.builder.build(), address)
            context.atlas()[address] = generated
        }
        return context.atlas()
    }

    companion object {
        private const val LIMIT_INITIAL_PROGRAMS = 10
    }
}
