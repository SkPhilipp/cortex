package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.collections.BackedInteger.Companion.ONE_32
import com.hileco.cortex.collections.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.symbolic.ProgramStoreZone.DISK
import com.hileco.cortex.symbolic.instructions.debug.NOOP
import com.hileco.cortex.symbolic.instructions.io.LOAD
import com.hileco.cortex.symbolic.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test
import java.util.*

class KnownLoadProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val configuration = mapOf(DISK to mapOf(ONE_32 to ZERO_32))
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                KnownLoadProcessor(configuration)
        ))
        val original = listOf(
                PUSH(ONE_32),
                LOAD(DISK)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Assert.assertEquals(instructions, listOf(
                NOOP(),
                PUSH(ZERO_32)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return KnownLoadProcessor(HashMap())
    }
}