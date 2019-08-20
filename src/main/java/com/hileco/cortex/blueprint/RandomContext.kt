package com.hileco.cortex.blueprint

import java.math.BigInteger
import java.util.*
import java.util.stream.IntStream

class RandomContext(private val seed: Long) {

    private val random: Random = Random(seed)

    fun <T> pick(chancedMap: Map<T, Double>): () -> T {
        val total = chancedMap.map { it.value }.sum()
        return {
            var choice = this.random.nextDouble() * total
            chancedMap.entries.first() {
                choice -= it.value
                choice <= 0
            }.key
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
}