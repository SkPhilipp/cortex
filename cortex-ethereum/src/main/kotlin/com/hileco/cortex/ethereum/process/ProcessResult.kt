package com.hileco.cortex.ethereum.process

data class ProcessResult(
        val output: List<String>,
        val errors: List<String>,
        val exitCode: Int
)
