package com.hileco.cortex.fuzzing

import com.hileco.cortex.collections.layer.LayeredVmMap
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.InstructionsBuilder
import java.util.*
import java.util.stream.IntStream

class ProgramGeneratorContext(seed: Long) {
    var builder: InstructionsBuilder = InstructionsBuilder()
    private val atlas: LayeredVmMap<BackedInteger, Program> = LayeredVmMap()
    private val random: Random = Random(seed)
    private val randomFuzzProgramLayout: () -> FuzzProgram
    private val randomFuzzFunctionLayout: () -> FuzzFunction

    init {
        randomFuzzProgramLayout = of(FuzzProgram.values())
        randomFuzzFunctionLayout = of(FuzzFunction.values())
    }

    private fun <T : Chanced> of(types: Array<T>): () -> T {
        val total = types.map { it.chance() }.sum()
        return {
            var choice = this.random.nextDouble() * total
            types.first {
                choice -= it.chance()
                choice <= 0
            }
        }
    }

    fun randomBetween(minimum: Int, maximum: Int): BackedInteger {
        return randomIntBetween(minimum, maximum).toBackedInteger()
    }

    fun random(): BackedInteger {
        val bytes = ByteArray(32)
        this.random.nextBytes(bytes)
        return BackedInteger(bytes)
    }

    fun randomIntBetween(minimum: Int, maximum: Int): Int {
        return this.random.nextInt(maximum - minimum) + minimum
    }

    fun forRandom(minimum: Int, maximum: Int, consumer: (Int) -> Unit) {
        val choice = randomIntBetween(minimum, maximum)
        IntStream.range(minimum, choice + 1).forEach(consumer)
    }

    fun atlas(): LayeredVmMap<BackedInteger, Program> {
        return this.atlas
    }

    fun randomFuzzProgram() {
        randomFuzzProgramLayout().implementation(this)
    }

    fun randomFuzzFunction() {
        randomFuzzFunctionLayout().implementation(this)
    }
}
