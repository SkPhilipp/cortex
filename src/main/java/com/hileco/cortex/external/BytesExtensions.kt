package com.hileco.cortex.external

/**
 * Converts a [String] to [ByteArray], inverse of [String.deserializeBytes].
 */
fun String.deserializeBytes(): ByteArray {
    val bytes = ByteArray(length / 2)
    for (i in 0 until bytes.size) {
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

/**
 * Converts a [Byte] to [String].
 */
fun Byte.serialize(): String {
    return String.format("%02x", this)
}
