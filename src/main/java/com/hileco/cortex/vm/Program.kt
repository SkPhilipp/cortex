package com.hileco.cortex.vm

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.vm.layer.LayeredBytes
import com.hileco.cortex.vm.layer.LayeredStack
import java.math.BigInteger

class Program(val instructions: List<Instruction>,
              val address: BigInteger = BigInteger.ZERO) {
    val storage: LayeredBytes = LayeredBytes()
    val transfers: LayeredStack<Pair<BigInteger, BigInteger>> = LayeredStack()

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        val size = instructions.size
        for (i in 0 until size) {
            stringBuilder.append(String.format("[%03d] %s\n", i, instructions[i]))
        }
        return "$stringBuilder"
    }
}
