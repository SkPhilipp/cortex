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
    fun testConstructorEmpty() {
        val backedInteger = BackedInteger("".deserializeBytes())

        Assert.assertEquals(0, backedInteger.asUInt())
    }

    @Test
    fun testConstructorRegularValue() {
        val backedInteger = BackedInteger("0xabcd".deserializeBytes())

        Assert.assertEquals(43981, backedInteger.asUInt())
    }

    @Test
    fun testConstructorFull() {
        val backedInteger = BackedInteger("0x0000000000000000000000000000000000000000000000000000000000000005".deserializeBytes())

        Assert.assertEquals(5, backedInteger.asUInt())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testConstructorPastLimit() {
        BackedInteger("0x000000000000000000000000000000000000000000000000000000000000000001".deserializeBytes())
    }

    @Test
    fun testAsUInt256Zero() {
        val value = 0.asUInt256()

        Assert.assertEquals(0, value.asUInt())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testAsUInt256Negative() {
        (-1).asUInt256()
    }

    @Test
    fun testAsUInt256Limit() {
        val value = Int.MAX_VALUE.asUInt256()
        Assert.assertEquals(Int.MAX_VALUE, value.asUInt())
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

    // test times (without overflow and with overflow)

    // test div (including divide 0)

    // test rem (including rem 0)

    // test compareTo (compare zero, one, and limit)

    // test equals, hashCode (put and retrieve from a map)

    // test toString on ZERO and LIMIT (compare with a fixed string)
}