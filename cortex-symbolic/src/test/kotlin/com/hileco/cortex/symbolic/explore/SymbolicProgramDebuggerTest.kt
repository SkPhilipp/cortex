package com.hileco.cortex.symbolic.explore

import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.vm.SymbolicProgram
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.instructions.math.ADD
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert.assertEquals
import org.junit.Test

internal class SymbolicProgramDebuggerTest {
    private fun symbolicProgramDebuggerFor(instructions: List<Instruction>): SymbolicProgramDebugger {
        val program = SymbolicProgram(instructions)
        val programContext = SymbolicProgramContext(program)
        val virtualMachine = SymbolicVirtualMachine(programContext)
        return SymbolicProgramDebugger(virtualMachine)
    }

    @Test
    fun testSteps() {
        val programDebugger = symbolicProgramDebuggerFor(listOf(
                PUSH(10.toBackedInteger()),
                PUSH(15.toBackedInteger()),
                ADD()
        ))

        programDebugger.stepTake()
        programDebugger.stepTake()
        programDebugger.stepTake()

        val topValue = programDebugger.virtualMachine.programs.first().stack.peek()
        assertEquals(Expression.Value(25.toBackedInteger()), topValue)
    }

    @Test
    fun testTakeJump() {
        val programDebugger = symbolicProgramDebuggerFor(listOf(
                JUMP_DESTINATION(),
                PUSH(10.toBackedInteger()),
                LOAD(CALL_DATA),
                PUSH(ZERO_32),
                JUMP_IF()
        ))

        programDebugger.stepTake()
        programDebugger.stepTake()
        programDebugger.stepTake()
        programDebugger.stepTake()
        programDebugger.stepTake()

        val instructionPosition = programDebugger.virtualMachine.programs.first().instructionPosition
        assertEquals(0, instructionPosition)
    }

    @Test
    fun testSkipJump() {
        val programDebugger = symbolicProgramDebuggerFor(listOf(
                JUMP_DESTINATION(),
                PUSH(10.toBackedInteger()),
                LOAD(CALL_DATA),
                PUSH(ZERO_32),
                JUMP_IF()
        ))

        programDebugger.stepTake()
        programDebugger.stepTake()
        programDebugger.stepTake()
        programDebugger.stepTake()
        programDebugger.stepSkip()

        val instructionPosition = programDebugger.virtualMachine.programs.first().instructionPosition
        assertEquals(5, instructionPosition)
    }
}
