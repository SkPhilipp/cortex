package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.collections.backed.BackedInteger.Companion.ONE_32
import com.hileco.cortex.symbolic.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

@Ignore
class DeadLoadProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        //    a: [PUSH 1, LOAD MEMORY, PUSH 0, PUSH 3, SAVE MEMORY] ==> [NOOP, NOOP, PUSH 2, PUSH 3, SAVE MEMORY]
        //    b: [PUSH 1, LOAD MEMORY, PUSH 0, PUSH 3, LOAD CALL_DATA, SAVE MEMORY] ==> No change
        val graphBuilder = GraphBuilder(listOf(
                DeadLoadProcessor()
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
        return DeadLoadProcessor()
    }
}