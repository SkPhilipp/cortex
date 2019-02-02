package com.hileco.cortex.server.serialization

import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.vm.layer.LayeredBytes

class LayeredBytesReader {
    fun read(input: String): LayeredBytes {
        val layeredBytes = LayeredBytes()
        input.split(",").forEach { entry ->
            val pairs = entry.split("=").zipWithNext()
            if (pairs.size != 1) {
                throw IllegalArgumentException("Argument '$pairs' could not be parsed to a call data entry")
            }
            val pair = pairs.first()
            val address = pair.first.toInt()
            val bytesToWrite = pair.second.toBigInteger().toByteArray()
            if (bytesToWrite.size > LOAD.SIZE) {
                throw IllegalArgumentException("Argument '${pair.second}' must fit in a byte array of size ${LOAD.SIZE}")
            }
            val alignmentOffset = LOAD.SIZE - bytesToWrite.size
            layeredBytes.write(address + alignmentOffset, bytesToWrite, bytesToWrite.size)
        }
        return layeredBytes
    }
}