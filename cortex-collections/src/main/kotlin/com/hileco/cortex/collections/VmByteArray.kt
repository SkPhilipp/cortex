package com.hileco.cortex.collections

interface VmByteArray : VmComponent<VmByteArray> {
    fun read(offset: Int, length: Int): ByteArray

    fun write(offset: Int, bytesToWrite: ByteArray, writeLength: Int = bytesToWrite.size)

    fun clear()

    fun limit(): Int

    fun size(): Int
}