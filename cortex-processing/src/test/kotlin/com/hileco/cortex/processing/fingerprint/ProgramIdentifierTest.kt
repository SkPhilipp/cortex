package com.hileco.cortex.processing.fingerprint

import com.hileco.cortex.ethereum.EthereumBarriers
import org.junit.Assert
import org.junit.Test


internal class ProgramIdentifierTest {
    @Test
    fun testIdentify() {
        val ethereumBarriers = EthereumBarriers()
        val ethereumBarrier = ethereumBarriers.byId("000")
        val programHistogramBuilder = ProgramHistogramBuilder()
        val histogram = programHistogramBuilder.histogram(ethereumBarrier.contractCode)

        val programIdentifier = ProgramIdentifier()
        val identifiedAs = programIdentifier.identify(histogram)

        Assert.assertEquals("barrier-000", identifiedAs)
    }
}