package com.hileco.cortex.processing.histogram

import com.hileco.cortex.ethereum.EthereumInstruction
import com.hileco.cortex.ethereum.EthereumParser
import com.hileco.cortex.collections.deserializeBytes
import com.hileco.cortex.collections.serialize

class ProgramHistogramBuilder {
    /**
     * Calculates a histogram containing the top instructions by opcode and their occurrences.
     */
    fun histogram(bytecode: String): String {
        val bytes = bytecode.deserializeBytes()
        val instructions = heuristicTrimEnd(ethereumParser.parse(bytes))
        val topInstructions = instructions.asSequence()
                .map { it.operation.code }
                .filterNotNull()
                .groupingBy { it }
                .eachCount()
                .map { it.key to it.value }
                .sortedBy { -it.second }
        return topInstructions.take(TOP_N_INSTRUCTIONS)
                .map { byteArrayOf(it.first, it.second.coerceAtMost(255).toByte()) }
                .fold(ByteArray(0)) { accumulator, entry -> accumulator.plus(entry) }
                .serialize()
    }

    /**
     * Removes the last few operations if a program is large enough to suspect the program to be a tool-compiled contract,
     * as often such contracts contain a hashes or signatures of the compiler, generally not JUMP-able and of no use to the execution of the program.
     */
    private fun heuristicTrimEnd(instructions: List<EthereumInstruction>): List<EthereumInstruction> {
        return if (instructions.size < 60) instructions else instructions.subList(0, instructions.size - 20)
    }

    companion object {
        private val ethereumParser = EthereumParser()
        private const val TOP_N_INSTRUCTIONS = 10
    }
}
