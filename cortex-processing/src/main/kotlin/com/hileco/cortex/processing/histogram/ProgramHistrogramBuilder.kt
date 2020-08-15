package com.hileco.cortex.processing.histogram

class ProgramHistrogramBuilder {
    fun hisogram(bytecode: String): String {
        return bytecode.hashCode().toString()
    }
}
