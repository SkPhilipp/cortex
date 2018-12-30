package com.hileco.cortex.vm.layer

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class LayeredBytesTest {
    @Test
    fun testReadWrite() {
        val bytes = LayeredBytes()
        bytes.write(0, byteArrayOf(1, 2, 3))
        val read = bytes.read(0, 3)
        assertArrayEquals(byteArrayOf(1, 2, 3), read)
    }

    @Test
    fun testClear() {
        val bytes = LayeredBytes()
        bytes.write(0, byteArrayOf(1, 2, 3))
        bytes.clear()
        val read = bytes.read(0, 3)
        assertArrayEquals(byteArrayOf(0, 0, 0), read)
    }

    @Test
    fun testEquals() {
        val bytesA = LayeredBytes()
        bytesA.write(0, byteArrayOf(1, 2, 3))
        val bytesB = LayeredBytes()
        bytesB.write(0, byteArrayOf(1, 2, 3))
        assertEquals(bytesA, bytesB)
    }
}