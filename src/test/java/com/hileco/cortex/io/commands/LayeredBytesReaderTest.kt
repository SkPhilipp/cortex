package com.hileco.cortex.io.commands

import com.hileco.cortex.instructions.io.LOAD
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class LayeredBytesReaderTest {
    @Test
    fun test() {
        val layeredBytesReader = LayeredBytesReader()
        val layeredBytes = layeredBytesReader.read("0=12345")
        val read = layeredBytes.read(0, LOAD.SIZE)
        Assert.assertEquals(BigInteger("12345"), BigInteger(read))
    }
    @Test
    fun testMany() {
        val layeredBytesReader = LayeredBytesReader()
        val layeredBytes = layeredBytesReader.read("0=12345,100=54321")
        val readFirst = layeredBytes.read(0, LOAD.SIZE)
        val readSecond = layeredBytes.read(100, LOAD.SIZE)
        Assert.assertEquals(BigInteger("12345"), BigInteger(readFirst))
        Assert.assertEquals(BigInteger("54321"), BigInteger(readSecond))
    }
}