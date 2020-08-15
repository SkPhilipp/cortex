package com.hileco.cortex.processing.geth

data class ProcessResult(
        val output: List<String>,
        val errors: List<String>,
        val exitCode: Int
)
