package com.hileco.cortex.vm

import com.hileco.cortex.vm.layer.LayeredMap
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger

class VirtualMachine(vararg programContexts: ProgramContext) {
    val programs: LayeredStack<ProgramContext> = LayeredStack()
    val atlas: LayeredMap<BigInteger, Program> = LayeredMap()
    val overflowLimit: BigInteger = NUMERICAL_LIMIT
    val underflowLimit: BigInteger = NUMERICAL_LIMIT
    val stackLimit: Long = java.lang.Long.MAX_VALUE
    var instructionsExecuted: Int = 0
    val instructionLimit: Int = INSTRUCTION_LIMIT

    init {
        for (programContext in programContexts) {
            programs.push(programContext)
        }
    }

    companion object {
        const val INSTRUCTION_LIMIT = 1000000
        val NUMERICAL_LIMIT: BigInteger = BigInteger(byteArrayOf(2)).pow(256).subtract(BigInteger.ONE)
    }
}
