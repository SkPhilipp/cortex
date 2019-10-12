package com.hileco.cortex.ethereum

import com.hileco.cortex.documentation.Documentation
import org.junit.Test

class EthereumOperationTest {
    @Test
    fun document() {
        Documentation.of(EthereumOperation::class.java.simpleName)
                .headingParagraph(EthereumOperation::class.java.simpleName)
                .paragraph("Mapping of Cortex-known Ethereum operations.")
                .source(EthereumOperation.values().asSequence()
                        .filter { it.code != null }
                        .joinToString(separator = "\n") { "${it.code?.serialize()}: ${it.name}" })
    }
}