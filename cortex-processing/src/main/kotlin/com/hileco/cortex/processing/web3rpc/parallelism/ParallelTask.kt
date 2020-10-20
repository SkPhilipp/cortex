package com.hileco.cortex.processing.web3rpc.parallelism

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class ParallelTask(fromInclusive: Int,
                   private val untilExclusive: Int,
                   private val task: (Int) -> Unit) {
    private val nextIndex = AtomicInteger(fromInclusive)
    private val resumeCounter = ResumeCounter(fromInclusive)

    fun resumeFrom(): Int {
        return resumeCounter.value()
    }

    fun run(threads: Int) {
        val executorService = Executors.newFixedThreadPool(threads)
        val threadCallables = mutableListOf<Callable<Unit>>()
        for (i in 0 until threads) {
            threadCallables.add(Callable {
                this.thread()
            })
        }
        executorService.invokeAll(threadCallables)
    }

    private fun thread() {
        var index: Int = nextIndex.getAndIncrement()
        while (index < untilExclusive) {
            try {
                task(index)
            } finally {
                resumeCounter.complete(index)
            }
            index = nextIndex.getAndIncrement()
        }
    }
}
