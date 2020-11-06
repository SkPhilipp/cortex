package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.collections.BackedInteger.Companion.ONE_32
import com.hileco.cortex.collections.toBackedInteger
import com.hileco.cortex.symbolic.ProgramException
import com.hileco.cortex.symbolic.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.symbolic.instructions.conditions.GREATER_THAN
import com.hileco.cortex.symbolic.instructions.conditions.IS_ZERO
import com.hileco.cortex.symbolic.instructions.debug.HALT
import com.hileco.cortex.symbolic.instructions.io.LOAD
import com.hileco.cortex.symbolic.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.symbolic.instructions.jumps.JUMP_IF
import com.hileco.cortex.symbolic.instructions.stack.PUSH
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

        Assert.assertEquals(instructions, listOf(
                PUSH(ONE_32)
        ))
    }

    override fun fuzzTestableProcessor(): Processor {
        return DeadPathConstraintProcessor()
    }
}