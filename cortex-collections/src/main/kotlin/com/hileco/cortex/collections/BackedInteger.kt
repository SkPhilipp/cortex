package com.hileco.cortex.collections

import java.lang.Byte.compareUnsigned
import java.lang.Byte.parseByte
import java.math.BigInteger
import java.util.*

/**
 * A wrapper around a byte array for working with bytes as types such as unsigned and signed integers.
 */
class BackedInteger : Comparable<BackedInteger> {
    private val backingArray: ByteArray

    constructor(sourceArray: ByteArray) {
        when {
            sourceArray.size <= 32 -> {
                val start = 32 - sourceArray.size
                backingArray = ByteArray(32) { index ->
                    if (index < start) {
                        0.toByte()
                    } else {
                        sourceArray[index - start]
                    }
                }
            }
            else -> {
                val pastLimit = sourceArray.size - 32
                for (i in 0 until pastLimit) {
                    if (sourceArray[i] != parseByte("00")) {
                        throw IllegalArgumentException("Arrays with values larger than 32 bytes can not be represented using BackedInteger.")
                    }
                }
                backingArray = ByteArray(32) { index ->
                    sourceArray[index + pastLimit]
                }
            }
        }
    }

    constructor(sourceList: List<Byte>) {
        when {
            sourceList.size <= 32 -> {
                val start = 32 - sourceList.size
                backingArray = ByteArray(32) { index ->
                    if (index < start) {
                        0.toByte()
                    } else {
                        sourceList[index - start]
                    }
                }
            }
            else -> {
                throw IllegalArgumentException("Arrays larger than 32 bytes can not be represented using BackedInteger.")
            }
        }
    }

    fun toInt(): Int {
        return BigInteger(1, backingArray).toInt()
    }

    fun toLong(): Long {
        return BigInteger(1, backingArray).toLong()
    }

    fun getBackingArray(): ByteArray {
        return backingArray
    }

    operator fun get(index: Int): Byte {
        return backingArray[index]
    }

    private fun last32BytesOf(byteArray: ByteArray): ByteArray {
        val resultOffset = if (byteArray.size > 32) byteArray.size - 32 else 0
        val resultLimit = 31.coerceAtMost(byteArray.size - 1)
        return byteArray.sliceArray(IntRange(resultOffset, resultOffset + resultLimit))
    }

    operator fun plus(other: BackedInteger): BackedInteger {
        val thisBigInt = BigInteger(1, backingArray)
        val otherBigInt = BigInteger(1, other.backingArray)
        val result = thisBigInt + otherBigInt
        return BackedInteger(last32BytesOf(result.toByteArray()))
    }

    operator fun minus(other: BackedInteger): BackedInteger {
        if (other > this) {
            val limitBigInteger = BigInteger(1, LIMIT_32.backingArray)
            val thisBigInteger = BigInteger(1, backingArray)
            val otherBigInt = BigInteger(1, other.backingArray)
            val result = limitBigInteger - ((otherBigInt - BigInteger.ONE) - thisBigInteger)
            return BackedInteger(last32BytesOf(result.toByteArray()))
        } else {
            val thisBigInt = BigInteger(1, backingArray)
            val otherBigInt = BigInteger(1, other.backingArray)
            val result = thisBigInt - otherBigInt
            return BackedInteger(last32BytesOf(result.toByteArray()))
        }
    }

    operator fun times(other: BackedInteger): BackedInteger {
        val thisBigInt = BigInteger(1, backingArray)
        val otherBigInt = BigInteger(1, other.backingArray)
        val result = thisBigInt * otherBigInt
        return BackedInteger(last32BytesOf(result.toByteArray()))
    }

    operator fun div(other: BackedInteger): BackedInteger {
        val thisBigInt = BigInteger(1, backingArray)
        val otherBigInt = BigInteger(1, other.backingArray)
        val result = thisBigInt / otherBigInt
        return BackedInteger(last32BytesOf(result.toByteArray()))
    }

    operator fun rem(other: BackedInteger): BackedInteger {
        val thisBigInt = BigInteger(1, backingArray)
        val otherBigInt = BigInteger(1, other.backingArray)
        val result = thisBigInt % otherBigInt
        return BackedInteger(last32BytesOf(result.toByteArray()))
    }

    fun pow(other: BackedInteger): BackedInteger {
        val thisBigInt = BigInteger(1, backingArray)
        val otherBigInt = BigInteger(1, other.backingArray)
        val limitBigInt = BigInteger(1, LIMIT_32.backingArray)
        val result = thisBigInt.modPow(otherBigInt, limitBigInt)
        return BackedInteger(last32BytesOf(result.toByteArray()))
    }

    override operator fun compareTo(other: BackedInteger): Int {
        val mismatchIndex = Arrays.mismatch(backingArray, other.backingArray)
        if (mismatchIndex < 0) {
            return 0
        } else {
            return compareUnsigned(backingArray[mismatchIndex], other.backingArray[mismatchIndex])
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BackedInteger) {
            return false
        }
        return this.backingArray.contentEquals(other.backingArray)
    }

    override fun hashCode(): Int {
        return backingArray.contentHashCode()
    }

    override fun toString(): String {
        return backingArray.serialize()
    }

    companion object {
        val ONE_32 = "0x0000000000000000000000000000000000000000000000000000000000000001".toBackedInteger()
        val ZERO_32 = "0x0000000000000000000000000000000000000000000000000000000000000000".toBackedInteger()
        val LIMIT_32 = "0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff".toBackedInteger()
    }
}
