package com.hileco.cortex.processing.web3rpc

import java.math.BigInteger

data class Web3Contract(
        val transactionHash: String,
        val bytecode: String,
        val address: String,
        val balance: BigInteger,
        val blockNumberCreated: BigInteger,
        val blockNumberLoaded: BigInteger
)
