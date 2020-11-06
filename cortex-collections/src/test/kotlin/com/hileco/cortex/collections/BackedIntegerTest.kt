package com.hileco.cortex.collections

import com.hileco.cortex.collections.BackedInteger.Companion.LIMIT_32
import com.hileco.cortex.collections.BackedInteger.Companion.ONE_32
import com.hileco.cortex.collections.BackedInteger.Companion.ZERO_32
import org.junit.Assert
import org.junit.Test


internal class BackedIntegerTest {

    @Test
    fun testIntAsUInt256() {
        val result = 0xffffff.toBackedInteger()

        Assert.assertEquals("0xffffff".toBackedInteger(), result)
    }

    @Test
    fun testLongAsUInt256() {
        val result = 0xffffffffffff.toBackedInteger()

        Assert.assertEquals("0xffffffffffff".toBackedInteger(), result)
    }

    @Test
    fun testConstructorEmpty() {
        val backedInteger = BackedInteger("".deserializeBytes())

        Assert.assertEquals(0, backedInteger.toInt())
    }

    @Test
    fun testConstructorRegularValue() {
        val backedInteger = "0xabcd".toBackedInteger()

        Assert.assertEquals(43981, backedInteger.toInt())
    }

    @Test
    fun testConstructorFull() {
        val backedInteger = "0x0000000000000000000000000000000000000000000000000000000000000005".toBackedInteger()

        Assert.assertEquals(5, backedInteger.toInt())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testConstructorPastLimit() {
        "0xff0000000000000000000000000000000000000000000000000000000000000001".toBackedInteger()
    }

    @Test
    fun testAsUInt256Zero() {
        val value = ZERO_32

        Assert.assertEquals(0, value.toInt())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAsUInt256Negative() {
        (-1).toBackedInteger()
    }

    @Test
    fun testAsUInt256Limit() {
        val value = Int.MAX_VALUE.toBackedInteger()
        Assert.assertEquals(Int.MAX_VALUE, value.toInt())
    }

    @Test
    fun testAsULong256Limit() {
        val value = Long.MAX_VALUE.toBackedInteger()
        Assert.assertEquals(Long.MAX_VALUE, value.toLong())
    }

    @Test
    fun testGet() {
        val value = "0x000f111e222d333c444b555a6669777888879996aaa5bbb4ccc3ddd2eee1fff0".toBackedInteger()

        Assert.assertEquals("0x00".deserializeByte(), value[0])
        Assert.assertEquals("0xf0".deserializeByte(), value[31])
    }

    @Test
    fun testPlusRegularValue() {
        val valueLeft = "0x8888".toBackedInteger()
        val valueRight = "0x7777".toBackedInteger()

        val result = valueLeft + valueRight

        Assert.assertEquals("0xffff".toBackedInteger(), result)
    }

    @Test
    fun testPlusOverflow() {
        val result = LIMIT_32 + ONE_32

        Assert.assertEquals(ZERO_32, result)
    }

    @Test
    fun testMinusRegularValue() {
        val valueLeft = "0x8888".toBackedInteger()
        val valueRight = "0x7777".toBackedInteger()

        val result = valueLeft - valueRight

        Assert.assertEquals("0x1111".toBackedInteger(), result)
    }

    @Test
    fun testMinusSameValue() {
        val valueLeft = "0x8888".toBackedInteger()
        val valueRight = "0x8888".toBackedInteger()

        val result = valueLeft - valueRight

        Assert.assertEquals("0x00".toBackedInteger(), result)
    }

    @Test
    fun testMinusOverflow() {
        val result = ZERO_32 - ONE_32

        Assert.assertEquals(LIMIT_32, result)
    }

    @Test
    fun testTimesRegularValue() {
        val leftValue = "0x1010".toBackedInteger()
        val rightValue = "0x0f".toBackedInteger()

        val result = leftValue * rightValue

        Assert.assertEquals("0xf0f0".toBackedInteger(), result)
    }

    /**
     * `0xff...ff` * `0x02` overflows to `0xff...fe`
     */
    @Test
    fun testTimesOverflow() {
        val leftValue = LIMIT_32
        val rightValue = "0x02".toBackedInteger()

        val result = leftValue * rightValue

        Assert.assertEquals(LIMIT_32 - ONE_32, result)
    }

    @Test
    fun testPowRegularValue() {
        val leftValue = "0x1000".toBackedInteger()
        val rightValue = "0x03".toBackedInteger()

        val result = leftValue.pow(rightValue)

        Assert.assertEquals("0x1000000000".toBackedInteger(), result)
    }

    @Test
    fun testDivRegularValue() {
        val leftValue = "0x1000".toBackedInteger()
        val rightValue = "0x10".toBackedInteger()

        val result = leftValue / rightValue

        Assert.assertEquals("0x0100".toBackedInteger(), result)
    }

    @Test
    fun testDivRounding() {
        val leftValue = "0x1001".toBackedInteger()
        val rightValue = "0x10".toBackedInteger()

        val result = leftValue / rightValue

        Assert.assertEquals("0x0100".toBackedInteger(), result)
    }

    @Test
    fun testRemRegularValue() {
        val leftValue = "0x100f".toBackedInteger()
        val rightValue = "0x10".toBackedInteger()

        val result = leftValue % rightValue

        Assert.assertEquals("0x0f".toBackedInteger(), result)
    }

    @Test
    fun testCompareTo() {
        Assert.assertTrue(ONE_32 > ZERO_32)
        Assert.assertTrue(LIMIT_32 > ZERO_32)
        Assert.assertTrue(LIMIT_32 > ONE_32)
        Assert.assertTrue(ZERO_32 < ONE_32)
        Assert.assertTrue(ZERO_32 < LIMIT_32)
        Assert.assertTrue(ONE_32 < LIMIT_32)
    }

    @Test
    fun testEquals() {
        Assert.assertTrue(ZERO_32 == ZERO_32)
        Assert.assertTrue(ONE_32 == ONE_32)
        Assert.assertTrue(LIMIT_32 == LIMIT_32)
        Assert.assertTrue(ZERO_32 != ONE_32)
        Assert.assertTrue(ONE_32 != LIMIT_32)
        Assert.assertTrue(LIMIT_32 != ZERO_32)
    }

    @Test
    fun testToString() {
        Assert.assertEquals("0000000000000000000000000000000000000000000000000000000000000000", ZERO_32.toString())
        Assert.assertEquals("0000000000000000000000000000000000000000000000000000000000000001", ONE_32.toString())
        Assert.assertEquals("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", LIMIT_32.toString())
    }
}