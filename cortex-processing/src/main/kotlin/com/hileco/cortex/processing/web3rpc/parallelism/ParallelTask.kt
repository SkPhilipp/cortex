package com.hileco.cortex.processing.web3rpc.parallelism

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

class ParallelTask(fromInclusive: Long,
                   private val untilExclusive: Long,
                   private val threads: Int,
                   private val task: (index: Long) -> Unit) {
    private val nextIndex = AtomicLong(fromInclusive)
    private val resumeCounter = ResumeCounter(fromInclusive)
    private val countDownLatch = CountDownLatch(threads)

    fun resumeFrom(): Long {
        return resumeCounter.value()
    }

    fun await() {
        countDownLatch.await()
    }

    fun isComplete(): Boolean {
        return countDownLatch.count == 0L
    }

    fun start() {
        val executorService = Executors.newFixedThreadPool(threads)
        for (i in 0 until threads) {
            executorService.submit {
                var index: Long = nextIndex.getAndIncrement()
                while (index < untilExclusive) {
                    try {
                        task(index)
                    } finally {
                        resumeCounter.complete(index)
                    }
                    index = nextIndex.getAndIncrement()
                }
                countDownLatch.countDown()
            }
        }
    }
}
