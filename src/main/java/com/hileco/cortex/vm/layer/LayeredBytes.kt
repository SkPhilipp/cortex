package com.hileco.cortex.vm.layer

import java.util.*

class LayeredBytes(val size: Int = DEFAULT_TOTAL_SIZE) {
    val bytes: ByteArray by lazy { ByteArray(size) }

    fun read(offset: Int, length: Int): ByteArray {
        return Arrays.copyOfRange(bytes, offset, offset + length)
    }

    fun clear() {
        System.arraycopy(ByteArray(size), 0, bytes, 0, size)
    }

    fun write(offset: Int, bytesToWrite: ByteArray, writeLength: Int = bytesToWrite.size) {
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
        const val DEFAULT_TOTAL_SIZE = 262144
    }
}
