package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.BarrierProgram
import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.analysis.edges.FlowType
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.math.DIVIDE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.concrete.ProgramStoreZone
import org.junit.Assert
import org.junit.Test

class PathStreamTest {
    @Test
    fun test() {
        val pathStream = PathStream(BarrierProgram.BARRIER_01.instructions, listOf(
                Flow(FlowType.PROGRAM_FLOW, 0, 9),
                Flow(FlowType.PROGRAM_END, 9, null)
        ))
        val instructions = pathStream.asSequence().map { it.instruction }.toList()
        val expexctedInstructions = listOf(
                PUSH(2),
                PUSH(1),
                LOAD(ProgramStoreZone.CALL_DATA),
                DIVIDE(),
                PUSH(12345),
                EQUALS(),
                IS_ZERO(),
                PUSH(10),
                JUMP_IF(),
                HALT(WINNER)
        )
        Assert.assertEquals(expexctedInstructions, instructions)
    }
}