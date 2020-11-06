package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.collections.BackedInteger.Companion.ONE_32
import com.hileco.cortex.collections.toBackedInteger
import com.hileco.cortex.symbolic.instructions.debug.NOOP
import com.hileco.cortex.symbolic.instructions.jumps.EXIT
import com.hileco.cortex.symbolic.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.symbolic.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class DeadEndProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val graphBuilder = GraphBuilder(listOf(
                DeadEndProcessor()
        ))
        val original = listOf(
                EXIT(),
                PUSH(10.toBackedInteger()),
                PUSH(ONE_32),
                JUMP_DESTINATION(),
                PUSH(10.toBackedInteger()),
                PUSH(ONE_32)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Assert.assertEquals(instructions, listOf(
                EXIT(),
                NOOP(),
                NOOP(),
                JUMP_DESTINATION(),
                PUSH(10.toBackedInteger()),
                PUSH(ONE_32)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return DeadEndProcessor()
    }
}