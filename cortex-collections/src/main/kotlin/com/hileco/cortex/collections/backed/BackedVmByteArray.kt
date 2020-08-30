package com.hileco.cortex.collections.backed

import com.hileco.cortex.collections.VmByteArray

class BackedVmByteArray(val size: Int = DEFAULT_TOTAL_SIZE) : VmByteArray {
    private val lazyBytes = lazy { ByteArray(size) }
    val bytes: ByteArray by lazyBytes
    private var writtenSize = 0

    override fun read(offset: Int, length: Int): ByteArray {
        return bytes.copyOfRange(offset, offset + length)
    }

    override fun write(offset: Int, bytesToWrite: ByteArray, writeLength: Int) {
        System.arraycopy(bytesToWrite, 0, bytes, offset, writeLength)
        writtenSize = writtenSize.coerceAtLeast(offset + writeLength)
    }

    override fun clear() {
        System.arraycopy(ByteArray(size), 0, bytes, 0, size)
    }

    override fun limit(): Int {
        return bytes.size
    }

    override fun size(): Int {
        return writtenSize
    }

    override fun close() {
    }

    override fun copy(): BackedVmByteArray {
        val clone = BackedVmByteArray(size)
        clone.write(0, bytes)
        return clone
    }

    override fun toString(): String {
        return "BackedByteArray(size=$size)"
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other is BackedVmByteArray && bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        return size.hashCode()
    }

    companion object {
        const val DEFAULT_TOTAL_SIZE = 262144
    }
}
