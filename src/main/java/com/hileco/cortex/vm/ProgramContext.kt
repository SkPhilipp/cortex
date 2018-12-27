package com.hileco.cortex.vm

import com.hileco.cortex.vm.layer.LayeredBytes
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger

data class ProgramContext(val program: Program,
                          var instructionsExecuted: Int = 0,
                          var instructionPosition: Int = 0,
                          val instructionLimit: Int = INSTRUCTION_LIMIT,
                          val stack: LayeredStack<ByteArray> = LayeredStack(),
                          val memory: LayeredBytes = LayeredBytes(),
                          var returnDataOffset: BigInteger = BigInteger.ZERO,
                          var returnDataSize: BigInteger = BigInteger.ZERO,
                          val callData: LayeredBytes = LayeredBytes()) {
    companion object {
        const val INSTRUCTION_LIMIT = 1000000
    }
}