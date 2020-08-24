package com.hileco.cortex.processing.fingerprint

import com.hileco.cortex.ethereum.EthereumBarrier
import com.hileco.cortex.ethereum.EthereumBarriers

class ProgramIdentifier {

    fun identify(histogram: String): String {
        val ethereumBarrier = BARRIER_HISTOGRAMS[histogram]
        if (ethereumBarrier != null) {
            return "barrier-${ethereumBarrier.id}"
        }
        return "unidentified"
    }

    companion object {
        private val BARRIER_HISTOGRAMS: Map<String, EthereumBarrier> by lazy {
            val ethereumBarriers = EthereumBarriers()
            val programHistogramBuilder = ProgramHistogramBuilder()
            ethereumBarriers.all().asSequence()
                    .map {
                        val histogram = programHistogramBuilder.histogram(it.contractCode)
                        histogram to it
                    }
                    .toMap()
        }
    }
}
