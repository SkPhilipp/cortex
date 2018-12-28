package com.hileco.cortex.fuzzer

import com.hileco.cortex.instructions.InstructionsBuilder
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.layer.LayeredMap
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger
import java.util.*
import java.util.stream.IntStream

class ProgramGeneratorContext(seed: Long) {
    private val instructionsBuilders: LayeredStack<InstructionsBuilder> = LayeredStack()
    private val atlas: LayeredMap<BigInteger, Program> = LayeredMap()
    private val random: Random = Random(seed)
    private val randomFuzzProgramLayout: () -> FuzzProgram
    private val randomFuzzFunctionLayout: () -> FuzzFunction
    private val randomFuzzExpression: () -> FuzzExpression

    init {
        randomFuzzProgramLayout = of(FuzzProgram.values())
        randomFuzzFunctionLayout = of(FuzzFunction.values())
        randomFuzzExpression = of(FuzzExpression.values())
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
        return BigInteger.valueOf(randomIntBetween(minimum, maximum).toLong())
    }

    fun random(): BigInteger {
        return BigInteger.valueOf(this.random.nextLong())
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

    fun randomFuzzProgram(): FuzzProgram {
        return randomFuzzProgramLayout()
    }

    fun randomFuzzFunction(): FuzzFunction {
        return randomFuzzFunctionLayout()
    }

    fun randomFuzzExpression(): FuzzExpression {
        return this.randomFuzzExpression()
    }

    fun pushBuilder(value: InstructionsBuilder) {
        instructionsBuilders.push(value)
    }

    fun popBuilder() {
        instructionsBuilders.pop()
    }

    fun currentBuilder(): InstructionsBuilder {
        return instructionsBuilders.peek()!!
    }
}
