package com.hileco.cortex.fuzzer

import com.hileco.cortex.instructions.ProgramBuilder
import com.hileco.cortex.vm.concrete.Program
import com.hileco.cortex.vm.layer.LayeredMap
import java.math.BigInteger
import java.util.*
import java.util.stream.IntStream

class ProgramGeneratorContext(seed: Long) {
    var builder: ProgramBuilder = ProgramBuilder()
    private val atlas: LayeredMap<BigInteger, Program> = LayeredMap()
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

    fun randomBetween(minimum: Int, maximum: Int): BigInteger {
        return randomIntBetween(minimum, maximum).toBigInteger()
    }

    fun random(): BigInteger {
        return this.random.nextLong().toBigInteger()
    }

    fun randomIntBetween(minimum: Int, maximum: Int): Int {
        return this.random.nextInt(maximum - minimum) + minimum
    }

    fun forRandom(minimum: Int, maximum: Int, consumer: (Int) -> Unit) {
        val choice = randomIntBetween(minimum, maximum)
        IntStream.range(minimum, choice + 1).forEach(consumer)
    }

    fun atlas(): LayeredMap<BigInteger, Program> {
        return this.atlas
    }

    fun randomFuzzProgram() {
        randomFuzzProgramLayout().implementation(this)
    }

    fun randomFuzzFunction() {
        randomFuzzFunctionLayout().implementation(this)
    }
}
