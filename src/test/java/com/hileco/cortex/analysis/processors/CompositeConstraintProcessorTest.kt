package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.math.DIVIDE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

@Ignore
class CompositeConstraintProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val graphBuilder = GraphBuilder(listOf(
                CompositeConstraintProcessor()
        ))
        val original = listOf(
                PUSH(2),
                PUSH(1),
                LOAD(CALL_DATA),
                DIVIDE(),
                PUSH(12345),
                EQUALS(),
                IS_ZERO(),
                PUSH(10),
                JUMP_IF(),
                HALT(WINNER),
                JUMP_DESTINATION()
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(CompositeConstraintProcessor::class.java.simpleName)
                .headingParagraph(CompositeConstraintProcessor::class.java.simpleName)
                .paragraph("Generates constraint composites for every conditional instruction, this information is then made available on the graph.")
                .paragraph("Program:").source(original)

        Assert.assertEquals(instructions, listOf(
                PUSH(1)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return CompositeConstraintProcessor()
    }
}