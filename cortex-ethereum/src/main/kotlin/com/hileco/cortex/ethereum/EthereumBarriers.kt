package com.hileco.cortex.ethereum

class EthereumBarriers {

    fun byId(id: String): EthereumBarrier {
        return BARRIERS.first { it.id == id }
    }

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
                        val contractSetupCode = EthereumBarrier::class.java.getResource("/contracts/barrier$identifier.sol.bin").readText().trim()
                        SETUP_PREFIX.find(contractSetupCode) ?: throw IllegalStateException("No known prefix in $contractSetupCode")
                        val contractCode = SETUP_PREFIX.replace(contractSetupCode, "6080604052")
                        val ethereumInstructions = ethereumParser.parse(contractCode.deserializeBytes())
                        val cortexInstructions = ethereumTranspiler.transpile(ethereumInstructions)
                        EthereumBarrier(identifier, "0x$contractCode", "0x$contractSetupCode", ethereumInstructions, cortexInstructions)
                    }
                    .toList()
        }

        private val SETUP_PREFIX = "6080604052.*?6080604052".toRegex()
    }
}
