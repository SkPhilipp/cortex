package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.analysis.edges.FlowType
import com.hileco.cortex.vm.ProgramException
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.conditions.EQUALS
import com.hileco.cortex.vm.instructions.conditions.IS_ZERO
import com.hileco.cortex.vm.instructions.debug.HALT
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.instructions.math.DIVIDE
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class PathStreamTest {
    @Test
    fun test() {
        val pathStream = PathStream(BARRIER_01.instructions, listOf(
                Flow(FlowType.PROGRAM_FLOW, 0, 9),
                Flow(FlowType.PROGRAM_END, 9, null)
        ))
        val instructions = pathStream.asSequence().map { it.instruction }.toList()
        val expectedInstructions = listOf(
                PUSH(2.toBackedInteger()),
                PUSH(1.toBackedInteger()),
                LOAD(ProgramStoreZone.CALL_DATA),
                DIVIDE(),
                PUSH(12345.toBackedInteger()),
                EQUALS(),
                IS_ZERO(),
                PUSH(10.toBackedInteger()),
                JUMP_IF(),
                HALT(ProgramException.Reason.WINNER)
        )
        Assert.assertEquals(expectedInstructions, instructions)
    }
}