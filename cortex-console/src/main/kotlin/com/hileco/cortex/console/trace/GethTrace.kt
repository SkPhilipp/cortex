package com.hileco.cortex.console.trace


data class GethTrace(
        val code: String,
        val input: String,
        val failed: Boolean,
        val gas: Int,
        val returnValue: String,
        val structLogs: List<GethTraceLog>
)
