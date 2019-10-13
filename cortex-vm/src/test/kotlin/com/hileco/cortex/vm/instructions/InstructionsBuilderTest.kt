package com.hileco.cortex.vm.instructions

import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramRunner
import com.hileco.cortex.vm.VirtualMachine
import com.hileco.cortex.vm.instructions.conditions.EQUALS
import com.hileco.cortex.vm.instructions.conditions.IS_ZERO
import com.hileco.cortex.vm.instructions.debug.NOOP
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.vm.instructions.jumps.JUMP_IF
import com.hileco.cortex.vm.instructions.math.ADD
import com.hileco.cortex.vm.instructions.stack.DUPLICATE
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class InstructionsBuilderTest {
    @Test
    fun testJump() {
        val program = Program(listOf(
                PUSH(byteArrayOf(123)),
                PUSH(byteArrayOf(123)),
                EQUALS(),
                PUSH(10),
                JUMP_IF(),
                NOOP(),
                NOOP(),
                NOOP(),
                NOOP(),
                NOOP(),
                JUMP_DESTINATION()
        ))
        val programContext = ProgramContext(program)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
        Assert.assertEquals(program.instructions.size, programContext.instructionPosition)
        Assert.assertEquals(6, programContext.instructionsExecuted)
    }

    @Test
    fun testNoJump() {
        val program = Program(listOf(
                PUSH(byteArrayOf(123)),
                PUSH(byteArrayOf(124)),
                EQUALS(),
                PUSH(10),
                JUMP_IF(),
                NOOP(),
                NOOP(),
                NOOP(),
                NOOP(),
                NOOP(),
                JUMP_DESTINATION()
        ))
        val programContext = ProgramContext(program)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
        Assert.assertEquals(program.instructions.size, programContext.instructionPosition)
        Assert.assertEquals(program.instructions.size, programContext.instructionsExecuted)
    }

    @Test
    fun testLoop() {
        val program = Program(listOf(
                PUSH(byteArrayOf(0)),
                JUMP_DESTINATION(),
                PUSH(byteArrayOf(1)),
                ADD(),
                DUPLICATE(0),
                PUSH(byteArrayOf(1, 0)),
                EQUALS(),
                IS_ZERO(),
                PUSH(1),
                JUMP_IF()
        ))
        val programContext = ProgramContext(program)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
        Assert.assertEquals(program.instructions.size, programContext.instructionPosition)
        Assert.assertEquals((program.instructions.size - 1) * 256 + 1, programContext.instructionsExecuted)
    }

}