package com.hileco.cortex.vm

import java.math.BigInteger

class ProgramConstants {
    companion object {
        const val STACK_LIMIT: Long = Long.MAX_VALUE
        const val INSTRUCTION_LIMIT = 1000000
        val OVERFLOW_LIMIT: BigInteger = BigInteger(byteArrayOf(2)).pow(256)
        val UNDERFLOW_LIMIT: BigInteger = BigInteger(byteArrayOf(2)).pow(256)
    }
}