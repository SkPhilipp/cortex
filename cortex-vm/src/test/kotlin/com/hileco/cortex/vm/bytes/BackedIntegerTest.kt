package com.hileco.cortex.vm.bytes

import com.hileco.cortex.collections.deserializeByte
import com.hileco.cortex.collections.deserializeBytes
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.LIMIT_32
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ONE_32
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
import org.junit.Assert
import org.junit.Test


internal class BackedIntegerTest {

    @Test
    fun testIntAsUInt256() {
        val result = 0xffffff.toBackedInteger()

        Assert.assertEquals(BackedInteger("0xffffff".deserializeBytes()), result)
    }

    @Test
    fun testLongAsUInt256() {
        val result = 0xffffffffffff.toBackedInteger()

        Assert.assertEquals(BackedInteger("0xffffffffffff".deserializeBytes()), result)
    }

    @Test
    fun testConstructorEmpty() {
        val backedInteger = BackedInteger("".deserializeBytes())

        Assert.assertEquals(0, backedInteger.toInt())
    }

    @Test
    fun testConstructorRegularValue() {
        val backedInteger = BackedInteger("0xabcd".deserializeBytes())

        Assert.assertEquals(43981, backedInteger.toInt())
    }

    @Test
    fun testConstructorFull() {
        val backedInteger = BackedInteger("0x0000000000000000000000000000000000000000000000000000000000000005".deserializeBytes())

        Assert.assertEquals(5, backedInteger.toInt())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testConstructorPastLimit() {
        BackedInteger("0xff0000000000000000000000000000000000000000000000000000000000000001".deserializeBytes())
    }

    @Test
    fun testAsUInt256Zero() {
        val value = 0.toBackedInteger()

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
        val value = BackedInteger("0x000f111e222d333c444b555a6669777888879996aaa5bbb4ccc3ddd2eee1fff0".deserializeBytes())

        Assert.assertEquals("0x00".deserializeByte(), value[0])
        Assert.assertEquals("0xf0".deserializeByte(), value[31])
    }

    @Test
    fun testPlusRegularValue() {
        val valueLeft = BackedInteger("0x8888".deserializeBytes())
        val valueRight = BackedInteger("0x7777".deserializeBytes())

        val result = valueLeft + valueRight

        Assert.assertEquals(BackedInteger("0xffff".deserializeBytes()), result)
    }

    @Test
    fun testPlusOverflow() {
        val result = LIMIT_32 + ONE_32

        Assert.assertEquals(ZERO_32, result)
    }

    @Test
    fun testMinusRegularValue() {
        val valueLeft = BackedInteger("0x8888".deserializeBytes())
        val valueRight = BackedInteger("0x7777".deserializeBytes())

        val result = valueLeft - valueRight

        Assert.assertEquals(BackedInteger("0x1111".deserializeBytes()), result)
    }

    @Test
    fun testMinusSameValue() {
        val valueLeft = BackedInteger("0x8888".deserializeBytes())
        val valueRight = BackedInteger("0x8888".deserializeBytes())

        val result = valueLeft - valueRight

        Assert.assertEquals(BackedInteger("0x00".deserializeBytes()), result)
    }

    @Test
    fun testMinusOverflow() {
        val result = ZERO_32 - ONE_32

        Assert.assertEquals(LIMIT_32, result)
    }

    @Test
    fun testTimesRegularValue() {
        val leftValue = BackedInteger("0x1010".deserializeBytes())
        val rightValue = BackedInteger("0x0f".deserializeBytes())

        val result = leftValue * rightValue

        Assert.assertEquals(BackedInteger("0xf0f0".deserializeBytes()), result)
    }

    /**
     * `0xff...ff` * `0x02` overflows to `0xff...fe`
     */
    @Test
    fun testTimesOverflow() {
        val leftValue = LIMIT_32
        val rightValue = BackedInteger("0x02".deserializeBytes())

        val result = leftValue * rightValue

        Assert.assertEquals(LIMIT_32 - ONE_32, result)
    }

    @Test
    fun testDivRegularValue() {
        val leftValue = BackedInteger("0x1000".deserializeBytes())
        val rightValue = BackedInteger("0x10".deserializeBytes())

        val result = leftValue / rightValue

        Assert.assertEquals(BackedInteger("0x0100".deserializeBytes()), result)
    }

    @Test
    fun testDivRounding() {
        val leftValue = BackedInteger("0x1001".deserializeBytes())
        val rightValue = BackedInteger("0x10".deserializeBytes())

        val result = leftValue / rightValue

        Assert.assertEquals(BackedInteger("0x0100".deserializeBytes()), result)
    }

    @Test
    fun testRemRegularValue() {
        val leftValue = BackedInteger("0x100f".deserializeBytes())
        val rightValue = BackedInteger("0x10".deserializeBytes())

        val result = leftValue % rightValue

        Assert.assertEquals(BackedInteger("0x0f".deserializeBytes()), result)
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