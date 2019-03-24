package com.hileco.cortex.vm.layer

import java.util.*

class LayeredBytes(val size: Int = DEFAULT_TOTAL_SIZE) : DelegateLayered<LayeredBytes>() {
    private val lazyBytes = lazy { ByteArray(size) }
    val bytes: ByteArray by lazyBytes

    @Synchronized
    fun read(offset: Int, length: Int): ByteArray {
        return Arrays.copyOfRange(bytes, offset, offset + length)
    }

    @Synchronized
    fun clear() {
        System.arraycopy(ByteArray(size), 0, bytes, 0, size)
    }

    @Synchronized
    fun write(offset: Int, bytesToWrite: ByteArray, writeLength: Int = bytesToWrite.size) {
        System.arraycopy(bytesToWrite, 0, bytes, offset, writeLength)
    }

    @Synchronized
    override fun recreateParent(): LayeredBytes {
        return LayeredBytes(size)
    }

    @Synchronized
    override fun branchDelegates(): LayeredBytes {
        val clone = LayeredBytes(size)
        clone.write(0, bytes)
        return clone
    }

    @Synchronized
    override fun disposeDelegates() {
    }

    override fun toString(): String {
        return "LayeredBytes(size=$size)"
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other is LayeredBytes && Arrays.equals(bytes, other.bytes)
    }

    override fun hashCode(): Int {
        return size.hashCode()
    }

    companion object {
        const val DEFAULT_TOTAL_SIZE = 262144
    }
}
