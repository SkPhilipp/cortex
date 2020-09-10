package com.hileco.cortex.analysis

import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ONE_32
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.vm.instructions.io.SAVE
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class VisualGraphPrettifierTest {

    private val visualGraphPrettifier = VisualGraphPrettifier()

    @Test
    fun testFormat() {
        val prettified = visualGraphPrettifier.format(PUSH(ONE_32))

        Assert.assertEquals("01", prettified)
    }

    @Test
    fun testParametersKnown() {
        val prettified = visualGraphPrettifier.prettify(listOf(
                PUSH(ZERO_32),
                PUSH(ONE_32),
                SAVE(MEMORY)
        ))

        Assert.assertEquals(listOf(
                "SAVE MEMORY(01, 00)"
        ), prettified)
    }
}