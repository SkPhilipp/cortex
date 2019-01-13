package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.conditions.GREATER_THAN
import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

@Ignore
class DeadPathConstraintProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val graphBuilder = GraphBuilder(listOf(
                CompositeConstraintProcessor(),
                DeadPathConstraintProcessor()
        ))
        val original = listOf(
                PUSH(1),
                LOAD(CALL_DATA),
                PUSH(1),
                LOAD(CALL_DATA),
                GREATER_THAN(),
                IS_ZERO(),
                PUSH(9),
                JUMP_IF(),
                HALT(WINNER),
                JUMP_DESTINATION()
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(DeadPathConstraintProcessor::class.java.simpleName)
                .headingParagraph(DeadPathConstraintProcessor::class.java.simpleName)
                .paragraph("Inspects constraint composites for conditions and combinations of sub-paths, marking them as possible or impossible ahead of time.")
                .paragraph("Program:").source(original)

        Assert.assertEquals(instructions, listOf(
                PUSH(1)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return DeadPathConstraintProcessor()
    }
}