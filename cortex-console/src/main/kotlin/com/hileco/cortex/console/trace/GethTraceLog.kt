package com.hileco.cortex.console.trace

data class GethTraceLog(
        val depth: Int,
        val gas: Int,
        val gasCost: Int,
        val memory: List<String>,
        val op: String,
        val pc: Int,
        val stack: List<String>,
        val storage: Map<String, String>
)
