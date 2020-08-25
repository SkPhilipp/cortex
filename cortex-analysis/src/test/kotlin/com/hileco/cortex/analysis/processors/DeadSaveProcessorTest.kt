package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

@Ignore
class DeadSaveProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        // a: [PUSH 0, PUSH 3, SAVE MEMORY, PUSH 1, LOAD MEMORY] ==> [NOOP, NOOP, NOOP, PUSH 1, LOAD MEMORY]
        //    (all save and load addresses known -> address known is known to never be read)
        // b: [PUSH 0, PUSH 3, LOAD CALL_DATA, SAVE MEMORY, PUSH 1, LOAD MEMORY] ==> No Change
        //    (one of the loads is dynamic, cannot predict)
        // c: [PUSH 1, LOAD MEMORY, PUSH 0, PUSH 3, LOAD CALL_DATA, SAVE MEMORY] ==> [PUSH 1, LOAD MEMORY, NOOP, NOOP, NOOP, NOOP]
        //    (the save is guaranteed to never be followed by a load; regardless of address it can be eliminated)
        val graphBuilder = GraphBuilder(listOf(
                DeadSaveProcessor()
        ))
        val original = listOf(
                PUSH(1.toBackedInteger())
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(DeadSaveProcessor::class.java.simpleName)
                .headingParagraph(DeadSaveProcessor::class.java.simpleName)
                .paragraph("Identifies write-only elements of program store zones, to eliminate known SAVEs.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                PUSH(1.toBackedInteger())
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return DeadSaveProcessor()
    }
}