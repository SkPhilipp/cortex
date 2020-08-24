package com.hileco.cortex.processing.fingerprint

import org.junit.Assert
import org.junit.Test

class ProgramHistogramBuilderTest {
    @Test
    fun testEmpty() {
        val histogram = programHistogramBuilder.histogram("")

        Assert.assertEquals("", histogram)
    }

    @Test
    fun testMultipleOccurrences() {
        val histogram = programHistogramBuilder.histogram("60806040526004")

        Assert.assertEquals("60" + "03" + "52" + "01", histogram)
    }

    @Test
    fun testOccurrenceLimit() {
        val bytecode = ("60" + "80").repeat(300)
        val histogram = programHistogramBuilder.histogram(bytecode)

        Assert.assertEquals("60" + "ff", histogram)
    }

    companion object {
        private val programHistogramBuilder = ProgramHistogramBuilder()
    }
}