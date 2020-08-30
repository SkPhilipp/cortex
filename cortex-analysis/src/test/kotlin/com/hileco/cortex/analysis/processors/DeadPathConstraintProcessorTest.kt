package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.ProgramException
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ONE_32
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.conditions.GREATER_THAN
import com.hileco.cortex.vm.instructions.conditions.IS_ZERO
import com.hileco.cortex.vm.instructions.debug.HALT
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

@Ignore
class DeadPathConstraintProcessorTest : ProcessorFuzzTest() {
    @Test
    fun process() {
        val graphBuilder = GraphBuilder(listOf(
                DeadPathConstraintProcessor()
        ))
        val original = listOf(
                PUSH(ONE_32),
                LOAD(CALL_DATA),
                PUSH(ONE_32),
                LOAD(CALL_DATA),
                GREATER_THAN(),
                IS_ZERO(),
                PUSH(9.toBackedInteger()),
                JUMP_IF(),
                HALT(ProgramException.Reason.WINNER),
                JUMP_DESTINATION()
        )
        val graph = graphBuilder.build(original)
        val instructions = graph.toInstructions()

        Documentation.of(DeadPathConstraintProcessor::class.java.simpleName)
                .headingParagraph(DeadPathConstraintProcessor::class.java.simpleName)
                .paragraph("Inspects constraint composites for conditions and combinations of sub-paths, marking them as possible or impossible ahead of time.")
                .paragraph("Program:").source(original)

        Assert.assertEquals(instructions, listOf(
                PUSH(ONE_32)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return DeadPathConstraintProcessor()
    }
}