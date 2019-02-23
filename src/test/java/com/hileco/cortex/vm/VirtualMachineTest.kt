package com.hileco.cortex.vm

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramBuilder
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.vm.ProgramStoreZone.DISK
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class VirtualMachineTest {
    @Test
    fun testSetup() {
        val program = Program(SETUP_INSTRUCTIONS)
        val programContext = ProgramContext(program)
        val originalVM = VirtualMachine(programContext)
        try {
            val programRunner = ProgramRunner(originalVM)
            programRunner.run()
        } catch (e: ProgramException) {
        }
        Assert.assertEquals(BigInteger(programContext.memory.read(200 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(2000))
        Assert.assertEquals(BigInteger(programContext.memory.read(300 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(3000))
        Assert.assertEquals(BigInteger(program.storage.read(200 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(2000))
        Assert.assertEquals(BigInteger(program.storage.read(300 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(3000))
        Assert.assertEquals(programContext.stack.size(), 5)
        Assert.assertEquals(BigInteger(programContext.stack.peek()), BigInteger.valueOf(1))
    }

    @Test
    fun testOverwrite() {
        val program = Program(COMPLETE_INSTRUCTIONS)
        val programContext = ProgramContext(program)
        val originalVM = VirtualMachine(programContext)
        try {
            val programRunner = ProgramRunner(originalVM)
            programRunner.run()
        } catch (e: ProgramException) {
        }
        try {
            originalVM.programs.peek().instructionPosition++
            val programRunner = ProgramRunner(originalVM)
            programRunner.run()
        } catch (e: ProgramException) {
        }
        Assert.assertEquals(BigInteger(programContext.memory.read(200 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(2000))
        Assert.assertEquals(BigInteger(programContext.memory.read(300 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(3999))
        Assert.assertEquals(BigInteger(programContext.memory.read(400 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(999))
        Assert.assertEquals(BigInteger(program.storage.read(200 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(2000))
        Assert.assertEquals(BigInteger(program.storage.read(300 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(3999))
        Assert.assertEquals(BigInteger(program.storage.read(400 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(999))
        Assert.assertEquals(programContext.stack.size(), 4)
        Assert.assertEquals(BigInteger(programContext.stack.peek()), BigInteger.valueOf(4))
    }

    @Test
    fun testBranching() {
        val program = Program(COMPLETE_INSTRUCTIONS)
        val programContext = ProgramContext(program)
        val originalVM = VirtualMachine(programContext)
        try {
            val programRunner = ProgramRunner(originalVM)
            programRunner.run()
        } catch (e: ProgramException) {
        }
        val branchVM = originalVM.branch()
        val branchProgramContext = branchVM.programs.peek()
        Assert.assertTrue(branchVM !== originalVM)
        Assert.assertTrue(branchProgramContext !== programContext)
        Assert.assertTrue(branchProgramContext.program !== programContext.program)
        try {
            originalVM.programs.peek().instructionPosition++
            val programRunner = ProgramRunner(originalVM)
            programRunner.run()
        } catch (e: ProgramException) {
        }
        Assert.assertEquals(BigInteger(branchProgramContext.memory.read(200 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(2000))
        Assert.assertEquals(BigInteger(branchProgramContext.memory.read(300 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(3000))
        Assert.assertEquals(BigInteger(branchProgramContext.program.storage.read(200 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(2000))
        Assert.assertEquals(BigInteger(branchProgramContext.program.storage.read(300 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(3000))
        Assert.assertEquals(branchProgramContext.stack.size(), 5)
        Assert.assertEquals(BigInteger(branchProgramContext.stack.peek()), BigInteger.valueOf(1))

        try {
            branchProgramContext.instructionPosition++
            val programRunner = ProgramRunner(branchVM)
            programRunner.run()
        } catch (e: ProgramException) {
        }
        Assert.assertEquals(BigInteger(branchProgramContext.memory.read(200 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(2000))
        Assert.assertEquals(BigInteger(branchProgramContext.memory.read(300 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(3999))
        Assert.assertEquals(BigInteger(branchProgramContext.memory.read(400 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(999))
        Assert.assertEquals(BigInteger(branchProgramContext.program.storage.read(200 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(2000))
        Assert.assertEquals(BigInteger(branchProgramContext.program.storage.read(300 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(3999))
        Assert.assertEquals(BigInteger(branchProgramContext.program.storage.read(400 * LOAD.SIZE, LOAD.SIZE)), BigInteger.valueOf(999))
        Assert.assertEquals(branchProgramContext.stack.size(), 4)
        Assert.assertEquals(BigInteger(branchProgramContext.stack.peek()), BigInteger.valueOf(4))
    }

    companion object {
        val SETUP_INSTRUCTIONS = with(ProgramBuilder()) {
            save(MEMORY, push(2000), push(200))
            save(MEMORY, push(3000), push(300))
            save(DISK, push(2000), push(200))
            save(DISK, push(3000), push(300))
            push(5)
            push(4)
            push(3)
            push(2)
            push(1)
            halt(WINNER)
            build()
        }
        private val OVERWRITE_INSTRUCTIONS = with(ProgramBuilder()) {
            save(MEMORY, add(load(MEMORY, push(300)), push(999)), push(300))
            save(MEMORY, push(999), push(400))
            save(DISK, add(load(DISK, push(300)), push(999)), push(300))
            save(DISK, push(999), push(400))
            swap(0, 4)
            swap(1, 3)
            pop()
            halt(WINNER)
            build()
        }
        val COMPLETE_INSTRUCTIONS = {
            val instructions = arrayListOf<Instruction>()
            instructions.addAll(SETUP_INSTRUCTIONS)
            instructions.addAll(OVERWRITE_INSTRUCTIONS)
            instructions.toList()
        }()
    }
}