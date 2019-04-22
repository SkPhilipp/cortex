package com.hileco.cortex.external

class EthereumParser {
    fun parse(bytecode: ByteArray): List<EthereumInstruction> {
        val instructions = arrayListOf<EthereumInstruction>()
        var i = 0
        while (i < bytecode.size) {
            val byte = bytecode[i]
            val operation = EthereumOperation.ofCode(byte)
            val instruction = if (operation == null) {
                EthereumInstruction(EthereumOperation.UNKNOWN, byteArrayOf(byte))
            } else {
                val input = bytecode.copyOfRange(i + 1, Math.min(i + 1 + operation.inputBytes, bytecode.size))
                i += operation.inputBytes
                EthereumInstruction(operation, input)
            }
            instructions.add(instruction)
            i++
        }
        return instructions.toList()
    }
}