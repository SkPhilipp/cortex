package com.hileco.cortex.processing.web3rpc

import com.hileco.cortex.processing.web3rpc.parallelism.ParallelTask
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

internal class ParallelTaskTest {

    @Test
    fun test() {
        val counter = AtomicInteger(20)
        val parallelTask = ParallelTask(0, 5) {
            counter.incrementAndGet()
        }

        parallelTask.run(3)

        Assert.assertEquals(25, counter.get())
        Assert.assertEquals(5, parallelTask.resumeFrom())
    }
}
