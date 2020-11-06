package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.collections.BackedInteger.Companion.ONE_32
import com.hileco.cortex.collections.toBackedInteger
import com.hileco.cortex.symbolic.ProgramException.Reason.WINNER
import com.hileco.cortex.symbolic.ProgramStoreZone.DISK
import com.hileco.cortex.symbolic.instructions.debug.HALT
import com.hileco.cortex.symbolic.instructions.debug.NOOP
import com.hileco.cortex.symbolic.instructions.io.SAVE
import com.hileco.cortex.symbolic.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class DeadStartProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val graphBuilder = GraphBuilder(listOf(
                DeadStartProcessor()
        ))
        val original = listOf(
                PUSH(10.toBackedInteger()),
                PUSH(ONE_32),
                HALT(WINNER)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Assert.assertEquals(instructions, listOf(NOOP(), NOOP(), HALT(WINNER)))
    }

    @Test
    fun processImplicitExit() {
        val graphBuilder = GraphBuilder(listOf(
                DeadStartProcessor()
        ))
        val original = listOf(
                PUSH(10.toBackedInteger()),
                PUSH(ONE_32)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Assert.assertEquals(instructions, listOf(NOOP(), NOOP()))
    }

    @Test
    fun processExplicitExitWithDiskChange() {
        val graphBuilder = GraphBuilder(listOf(
                DeadStartProcessor()
        ))
        val original = listOf(
                PUSH(10.toBackedInteger()),
                PUSH(ONE_32),
                SAVE(DISK),
                HALT(WINNER)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Assert.assertEquals(instructions, original)
    }

    override fun fuzzTestableProcessor(): Processor {
        return DeadStartProcessor()
    }
}