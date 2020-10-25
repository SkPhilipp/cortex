package com.hileco.cortex.processing.database

enum class Network(
        val internalName: String,
        val blockchain: String,
        val blockchainId: String,
        val defaultEndpoint: String
) {
    ETHEREUM_MAINNET("mainnet", "Ethereum", "1", "http://localhost:8546"),
    ETHEREUM_ROPSTEN("ropsten", "Ethereum", "3", "https://ropsten.infura.io/v3/03bd45a1617a4748adc45da65ac9bc1f"),
    ETHEREUM_RINKEBY("rinkeby", "Ethereum", "4", "https://rinkeby.infura.io/v3/03bd45a1617a4748adc45da65ac9bc1f"),
    ETHEREUM_GOERLI("goerli", "Ethereum", "5", "https://goerli.infura.io/v3/03bd45a1617a4748adc45da65ac9bc1f"),
    ETHEREUM_KOVAN("kovan", "Ethereum", "42", "https://kovan.infura.io/v3/03bd45a1617a4748adc45da65ac9bc1f"),
    ETHEREUM_CLASSIC("classic", "Ethereum", "61", "http://localhost:8545"),
    ETHEREUM_MORDEN("morden", "Ethereum", "62", "http://localhost:8545"),
    ETHEREUM_PRIVATE("private", "Ethereum", "1337", "http://localhost:8545");

    override fun toString(): String {
        return internalName
    }
}
