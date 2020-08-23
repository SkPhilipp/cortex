package com.hileco.cortex.ethereum

class EthereumBarriers {

    fun all(): List<EthereumBarrier> {
        return BARRIERS
    }

    companion object {
        private val BARRIERS: List<EthereumBarrier> by lazy {
            val ethereumParser = EthereumParser()
            val ethereumTranspiler = EthereumTranspiler()
            IntRange(0, 16).asSequence()
                    .map {
                        val identifier = "$it".padStart(3, '0')
                        val code = EthereumBarrier::class.java.getResource("/contracts/barrier$identifier.sol.bin").readText().trim()
                        val ethereumInstructions = ethereumParser.parse(code.deserializeBytes())
                        val cortexInstructions = ethereumTranspiler.transpile(ethereumInstructions)
                        EthereumBarrier(identifier, "0x$code", ethereumInstructions, cortexInstructions)
                    }
                    .toList()
        }
    }
}
