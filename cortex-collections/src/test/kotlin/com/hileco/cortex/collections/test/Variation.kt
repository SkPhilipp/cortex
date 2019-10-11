package com.hileco.cortex.collections.test

import java.util.*

class Variation(seed: Long) {
    private var random: Random? = Random(seed)

    fun seed(seed: Long) {
        this.random = Random(seed)
    }

    fun maybe(runnable: () -> Unit) {
        if (random!!.nextBoolean()) {
            runnable()
        }
    }

    companion object {
        fun fuzzed(times: Long, fuzzee: (Variation) -> Unit) {
            for (i in 0L until times) {
                try {
                    val variation = Variation(i)
                    fuzzee(variation)
                } catch (t: Throwable) {
                    throw IllegalStateException(String.format("Exception using variation seed %d of %d", i, times), t)
                }

            }
        }
    }
}