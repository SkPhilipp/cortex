package com.hileco.cortex.vm.layer

import java.util.*

class LayeredBytes(var bytes: ByteArray = ByteArray(DEFAULT_TOTAL_SIZE)) {
    fun read(offset: Int, length: Int): ByteArray {
        return Arrays.copyOfRange(bytes, offset, offset + length)
    }

    fun clear() {
        bytes = ByteArray(DEFAULT_TOTAL_SIZE)
    }

    fun write(offset: Int, bytesToWrite: ByteArray) {
        this.write(offset, bytesToWrite, bytesToWrite.size)
    }

    fun write(offset: Int, bytesToWrite: ByteArray, writeLength: Int) {
        System.arraycopy(bytesToWrite, 0, bytes, offset, writeLength)
    }

    override fun toString(): String {
        return "LayeredBytes{size ${bytes.size}}"
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other is LayeredBytes && Arrays.equals(bytes, other.bytes)
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(bytes)
    }

    companion object {
        const val DEFAULT_TOTAL_SIZE = 8192
    }
}
