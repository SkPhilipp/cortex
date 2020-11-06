package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.collections.BackedInteger.Companion.ONE_32
import com.hileco.cortex.symbolic.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

@Ignore
class InstructionHoistProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        // [PUSH 1, PUSH 4, JUMP, JUMP_DESTINATION, NOOP] ==> [NOOP, PUSH 4, JUMP, JUMP_DESTINATION, PUSH 1]
        val graphBuilder = GraphBuilder(listOf(
                InstructionHoistProcessor()
        ))
        val original = listOf(
                PUSH(ONE_32)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Assert.assertEquals(instructions, listOf(
                PUSH(ONE_32)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return InstructionHoistProcessor()
    }
}