package com.hileco.cortex.external

import org.junit.Assert
import org.junit.Test

class BytesExtensionsTest {
    @Test
    fun test() {
        val sample = "608060405234801561001057600080fd5b506104fd806100206000396000f3fe6080604052"
        val converted = sample.deserializeBytes().serialize()

        Assert.assertEquals(sample, converted)
    }
}