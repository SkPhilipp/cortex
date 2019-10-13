package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.instructions.stack.PUSH
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
                PUSH(1)
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(DeadLoadProcessor::class.java.simpleName)
                .headingParagraph(DeadLoadProcessor::class.java.simpleName)
                .paragraph("Identifies read-only elements of program store zones, to eliminate known LOADs.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions)

        Assert.assertEquals(instructions, listOf(
                PUSH(1)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return DeadLoadProcessor()
    }
}