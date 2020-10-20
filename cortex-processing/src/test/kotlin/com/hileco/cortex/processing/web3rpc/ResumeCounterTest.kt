package com.hileco.cortex.processing.web3rpc

import com.hileco.cortex.processing.web3rpc.parallelism.ResumeCounter
import org.junit.Assert
import org.junit.Test

internal class ResumeCounterTest {

    @Test
    fun testInitialValue() {
        val resumeCounter = ResumeCounter(5)

        Assert.assertEquals(5, resumeCounter.value())
    }

    @Test
    fun testCompletionAligned() {
        val resumeCounter = ResumeCounter(5)
        resumeCounter.complete(5)
        resumeCounter.complete(6)
        resumeCounter.complete(7)
        resumeCounter.complete(8)

        Assert.assertEquals(9, resumeCounter.value())
    }

    @Test
    fun testCompletionSkippedFirst() {
        val resumeCounter = ResumeCounter(5)
        resumeCounter.complete(6)
        resumeCounter.complete(7)
        resumeCounter.complete(8)

        Assert.assertEquals(5, resumeCounter.value())
    }

    @Test
    fun testCompletionSkipped() {
        val resumeCounter = ResumeCounter(5)
        resumeCounter.complete(5)
        resumeCounter.complete(6)
        resumeCounter.complete(8)

        Assert.assertEquals(7, resumeCounter.value())
    }

    @Test
    fun testCompletionOutOfOrder() {
        val resumeCounter = ResumeCounter(0)
        resumeCounter.complete(1)
        resumeCounter.complete(2)
        resumeCounter.complete(0)

        Assert.assertEquals(3, resumeCounter.value())
    }
}