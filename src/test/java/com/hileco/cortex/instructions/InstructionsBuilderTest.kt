package com.hileco.cortex.instructions

import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.math.ADD
import com.hileco.cortex.instructions.stack.DUPLICATE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class InstructionsBuilderTest {
    @Test
    @Throws(ProgramException::class)
    fun testJump() {
        val program = Program(listOf(
                PUSH(byteArrayOf(123)),
                PUSH(byteArrayOf(123)),
                EQUALS(),
                PUSH(BigInteger.valueOf(10L).toByteArray()),
                JUMP_IF(),
                NOOP(),
                NOOP(),
                NOOP(),
                NOOP(),
                NOOP(),
                JUMP_DESTINATION()
        ))
        val programContext = ProgramContext(program)
        val processContext = VirtualMachine(programContext)
        val programRunner = ProgramRunner(processContext)
        programRunner.run()
        Assert.assertEquals(program.instructions.size.toLong(), programContext.instructionPosition.toLong())
        Assert.assertEquals(6, programContext.instructionsExecuted.toLong())
    }

    @Test
    @Throws(ProgramException::class)
    fun testNoJump() {
        val program = Program(listOf(
                PUSH(byteArrayOf(123)),
                PUSH(byteArrayOf(124)),
                EQUALS(),
                PUSH(BigInteger.valueOf(10L).toByteArray()),
                JUMP_IF(),
                NOOP(),
                NOOP(),
                NOOP(),
                NOOP(),
                NOOP(),
                JUMP_DESTINATION()
        ))
        val programContext = ProgramContext(program)
        val processContext = VirtualMachine(programContext)
        val programRunner = ProgramRunner(processContext)
        programRunner.run()
        Assert.assertEquals(program.instructions.size.toLong(), programContext.instructionPosition.toLong())
        Assert.assertEquals(program.instructions.size.toLong(), programContext.instructionsExecuted.toLong())
    }

    @Test
    @Throws(ProgramException::class)
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
                PUSH(BigInteger.valueOf(1L).toByteArray()),
                JUMP_IF()
        ))
        val programContext = ProgramContext(program)
        val processContext = VirtualMachine(programContext)
        val programRunner = ProgramRunner(processContext)
        programRunner.run()
        Assert.assertEquals(program.instructions.size.toLong(), programContext.instructionPosition.toLong())
        Assert.assertEquals(((program.instructions.size - 1) * 256 + 1).toLong(), programContext.instructionsExecuted.toLong())
    }

}
