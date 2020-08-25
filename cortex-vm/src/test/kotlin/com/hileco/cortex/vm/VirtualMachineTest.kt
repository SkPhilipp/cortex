package com.hileco.cortex.vm

import com.hileco.cortex.vm.ProgramStoreZone.DISK
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.bytes.asUInt256
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.InstructionsBuilder
import com.hileco.cortex.vm.instructions.io.LOAD
import org.junit.Assert
import org.junit.Test

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
        Assert.assertEquals(BackedInteger(programContext.memory.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.asUInt256())
        Assert.assertEquals(BackedInteger(programContext.memory.read(300 * LOAD.SIZE, LOAD.SIZE)), 3000.asUInt256())
        Assert.assertEquals(BackedInteger(program.storage.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.asUInt256())
        Assert.assertEquals(BackedInteger(program.storage.read(300 * LOAD.SIZE, LOAD.SIZE)), 3000.asUInt256())
        Assert.assertEquals(programContext.stack.size(), 5)
        Assert.assertEquals(programContext.stack.peek(), 1.asUInt256())
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
            originalVM.programs.last().instructionPosition++
            val programRunner = ProgramRunner(originalVM)
            programRunner.run()
        } catch (e: ProgramException) {
        }
        Assert.assertEquals(BackedInteger(programContext.memory.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.asUInt256())
        Assert.assertEquals(BackedInteger(programContext.memory.read(300 * LOAD.SIZE, LOAD.SIZE)), 3999.asUInt256())
        Assert.assertEquals(BackedInteger(programContext.memory.read(400 * LOAD.SIZE, LOAD.SIZE)), 999.asUInt256())
        Assert.assertEquals(BackedInteger(program.storage.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.asUInt256())
        Assert.assertEquals(BackedInteger(program.storage.read(300 * LOAD.SIZE, LOAD.SIZE)), 3999.asUInt256())
        Assert.assertEquals(BackedInteger(program.storage.read(400 * LOAD.SIZE, LOAD.SIZE)), 999.asUInt256())
        Assert.assertEquals(programContext.stack.size(), 4)
        Assert.assertEquals(programContext.stack.peek(), 4.asUInt256())
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
        val branchVM = originalVM.copy()
        val branchProgramContext = branchVM.programs.last()
        Assert.assertTrue(branchVM !== originalVM)
        Assert.assertTrue(branchProgramContext !== programContext)
        Assert.assertTrue(branchProgramContext.program !== programContext.program)
        try {
            originalVM.programs.last().instructionPosition++
            val programRunner = ProgramRunner(originalVM)
            programRunner.run()
        } catch (e: ProgramException) {
        }
        Assert.assertEquals(BackedInteger(branchProgramContext.memory.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.asUInt256())
        Assert.assertEquals(BackedInteger(branchProgramContext.memory.read(300 * LOAD.SIZE, LOAD.SIZE)), 3000.asUInt256())
        Assert.assertEquals(BackedInteger(branchProgramContext.program.storage.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.asUInt256())
        Assert.assertEquals(BackedInteger(branchProgramContext.program.storage.read(300 * LOAD.SIZE, LOAD.SIZE)), 3000.asUInt256())
        Assert.assertEquals(branchProgramContext.stack.size(), 5)
        Assert.assertEquals(branchProgramContext.stack.peek(), 1.asUInt256())

        try {
            branchProgramContext.instructionPosition++
            val programRunner = ProgramRunner(branchVM)
            programRunner.run()
        } catch (e: ProgramException) {
        }
        Assert.assertEquals(BackedInteger(branchProgramContext.memory.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.asUInt256())
        Assert.assertEquals(BackedInteger(branchProgramContext.memory.read(300 * LOAD.SIZE, LOAD.SIZE)), 3999.asUInt256())
        Assert.assertEquals(BackedInteger(branchProgramContext.memory.read(400 * LOAD.SIZE, LOAD.SIZE)), 999.asUInt256())
        Assert.assertEquals(BackedInteger(branchProgramContext.program.storage.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.asUInt256())
        Assert.assertEquals(BackedInteger(branchProgramContext.program.storage.read(300 * LOAD.SIZE, LOAD.SIZE)), 3999.asUInt256())
        Assert.assertEquals(BackedInteger(branchProgramContext.program.storage.read(400 * LOAD.SIZE, LOAD.SIZE)), 999.asUInt256())
        Assert.assertEquals(branchProgramContext.stack.size(), 4)
        Assert.assertEquals(branchProgramContext.stack.peek(), 4.asUInt256())
    }

    companion object {
        val SETUP_INSTRUCTIONS = with(InstructionsBuilder()) {
            save(MEMORY, push(2000.asUInt256()), push(200.asUInt256()))
            save(MEMORY, push(3000.asUInt256()), push(300.asUInt256()))
            save(DISK, push(2000.asUInt256()), push(200.asUInt256()))
            save(DISK, push(3000.asUInt256()), push(300.asUInt256()))
            push(5.asUInt256())
            push(4.asUInt256())
            push(3.asUInt256())
            push(2.asUInt256())
            push(1.asUInt256())
            halt(ProgramException.Reason.WINNER)
            build()
        }
        private val OVERWRITE_INSTRUCTIONS = with(InstructionsBuilder()) {
            save(MEMORY, add(load(MEMORY, push(300.asUInt256())), push(999.asUInt256())), push(300.asUInt256()))
            save(MEMORY, push(999.asUInt256()), push(400.asUInt256()))
            save(DISK, add(load(DISK, push(300.asUInt256())), push(999.asUInt256())), push(300.asUInt256()))
            save(DISK, push(999.asUInt256()), push(400.asUInt256()))
            swap(0, 4)
            swap(1, 3)
            pop()
            halt(ProgramException.Reason.WINNER)
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