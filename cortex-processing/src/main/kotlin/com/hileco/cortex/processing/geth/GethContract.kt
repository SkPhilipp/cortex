package com.hileco.cortex.processing.geth

import java.math.BigDecimal

data class GethContract(
        val bytecode: String,
        val transactionHash: String,
        val address: String,
        val currency: BigDecimal
)
