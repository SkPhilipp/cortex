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
        return String.format("LayeredBytes{size %d}", bytes.size)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this.javaClass != other.javaClass) return false
        val that = other as LayeredBytes
        return Arrays.equals(bytes, that.bytes)
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(bytes)
    }

    companion object {
        const val DEFAULT_TOTAL_SIZE = 8192
    }
}
