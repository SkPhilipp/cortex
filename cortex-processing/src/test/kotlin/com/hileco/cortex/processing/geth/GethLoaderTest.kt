package com.hileco.cortex.processing.geth

import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test


class GethLoaderTest {

    private val gethLoader = GethLoader()

    @Ignore("Requires a setup for GethLoader to be functional, should only be run when modifying GethLoader.")
    @Test
    fun testInvokeGeth() {
        val result = gethLoader.executeGeth("geth-loader-test.js", 0, 1, "x", "y")

        assertEquals(0, result[0].asInt())
        assertEquals(1, result[1].asInt())
        assertEquals("x", result[2].asText())
        assertEquals("y", result[3].asText())
    }
}
