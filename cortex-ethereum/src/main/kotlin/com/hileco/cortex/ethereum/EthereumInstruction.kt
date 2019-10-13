package com.hileco.cortex.ethereum

import java.util.*

data class EthereumInstruction(val operation: EthereumOperation, val input: ByteArray) {
    override fun equals(other: Any?): Boolean {
        return other is EthereumInstruction
                && Objects.equals(operation, other.operation)
                && Arrays.equals(input, other.input)
    }

    override fun hashCode(): Int {
        var result = operation.hashCode()
        result = 31 * result + input.contentHashCode()
        return result
    }

    override fun toString(): String {
        return if (input.isNotEmpty()) {
            "$operation 0x${input.serialize()}"
        } else {
            "$operation"
        }
    }
}