package com.hileco.cortex.collections

import java.math.BigInteger

/**
 * Converts a [String] to [ByteArray], inverse of [String.deserializeBytes].
 */
fun String.deserializeBytes(): ByteArray {
    if (this.length % 2 != 0) {
        return ("0$this").deserializeBytes()
    }
    if (this.length >= 2 && this.substring(0, 2) == "0x") {
        return this.substring(2).deserializeBytes()
    }
    val bytes = ByteArray(length / 2)
    for (i in bytes.indices) {
        val index = i * 2
        val integer = Integer.parseInt(this.substring(index, index + 2), 16)
        bytes[i] = integer.toByte()
    }
    return bytes
}

/**
 * Converts a [String] to [Byte].
 */
fun String.deserializeByte(): Byte {
    return deserializeBytes().single()
}

/**
 * Converts a [ByteArray] to [String], inverse of [ByteArray.serialize].
 */
fun ByteArray.serialize(): String {
    return this.asSequence()
            .map { String.format("%02x", it) }
            .joinToString(separator = "") { it }
}

fun String.toBackedInteger(): BackedInteger {
    return BackedInteger(this.deserializeBytes())
}

fun Long.toBackedInteger(): BackedInteger {
    if (this < 0) {
        throw IllegalArgumentException("Cannot cast negative values to BackedInteger")
    }
    val toByteArray = BigInteger.valueOf(this).toByteArray()
    return BackedInteger(toByteArray)
}

fun Int.toBackedInteger(): BackedInteger {
    if (this < 0) {
        throw IllegalArgumentException("Cannot cast negative values to BackedInteger")
    }
    val toByteArray = BigInteger.valueOf(this.toLong()).toByteArray()
    return BackedInteger(toByteArray)
}
