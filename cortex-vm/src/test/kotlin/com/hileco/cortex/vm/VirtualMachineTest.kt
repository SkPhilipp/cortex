package com.hileco.cortex.vm

import com.hileco.cortex.vm.ProgramStoreZone.DISK
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.bytes.toBackedInteger
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
        Assert.assertEquals(BackedInteger(programContext.memory.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.toBackedInteger())
        Assert.assertEquals(BackedInteger(programContext.memory.read(300 * LOAD.SIZE, LOAD.SIZE)), 3000.toBackedInteger())
        Assert.assertEquals(BackedInteger(program.storage.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.toBackedInteger())
        Assert.assertEquals(BackedInteger(program.storage.read(300 * LOAD.SIZE, LOAD.SIZE)), 3000.toBackedInteger())
        Assert.assertEquals(programContext.stack.size(), 5)
        Assert.assertEquals(programContext.stack.peek(), 1.toBackedInteger())
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
        Assert.assertEquals(BackedInteger(programContext.memory.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.toBackedInteger())
        Assert.assertEquals(BackedInteger(programContext.memory.read(300 * LOAD.SIZE, LOAD.SIZE)), 3999.toBackedInteger())
        Assert.assertEquals(BackedInteger(programContext.memory.read(400 * LOAD.SIZE, LOAD.SIZE)), 999.toBackedInteger())
        Assert.assertEquals(BackedInteger(program.storage.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.toBackedInteger())
        Assert.assertEquals(BackedInteger(program.storage.read(300 * LOAD.SIZE, LOAD.SIZE)), 3999.toBackedInteger())
        Assert.assertEquals(BackedInteger(program.storage.read(400 * LOAD.SIZE, LOAD.SIZE)), 999.toBackedInteger())
        Assert.assertEquals(programContext.stack.size(), 4)
        Assert.assertEquals(programContext.stack.peek(), 4.toBackedInteger())
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
        Assert.assertEquals(BackedInteger(branchProgramContext.memory.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.toBackedInteger())
        Assert.assertEquals(BackedInteger(branchProgramContext.memory.read(300 * LOAD.SIZE, LOAD.SIZE)), 3000.toBackedInteger())
        Assert.assertEquals(BackedInteger(branchProgramContext.program.storage.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.toBackedInteger())
        Assert.assertEquals(BackedInteger(branchProgramContext.program.storage.read(300 * LOAD.SIZE, LOAD.SIZE)), 3000.toBackedInteger())
        Assert.assertEquals(branchProgramContext.stack.size(), 5)
        Assert.assertEquals(branchProgramContext.stack.peek(), 1.toBackedInteger())

        try {
            branchProgramContext.instructionPosition++
            val programRunner = ProgramRunner(branchVM)
            programRunner.run()
        } catch (e: ProgramException) {
        }
        Assert.assertEquals(BackedInteger(branchProgramContext.memory.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.toBackedInteger())
        Assert.assertEquals(BackedInteger(branchProgramContext.memory.read(300 * LOAD.SIZE, LOAD.SIZE)), 3999.toBackedInteger())
        Assert.assertEquals(BackedInteger(branchProgramContext.memory.read(400 * LOAD.SIZE, LOAD.SIZE)), 999.toBackedInteger())
        Assert.assertEquals(BackedInteger(branchProgramContext.program.storage.read(200 * LOAD.SIZE, LOAD.SIZE)), 2000.toBackedInteger())
        Assert.assertEquals(BackedInteger(branchProgramContext.program.storage.read(300 * LOAD.SIZE, LOAD.SIZE)), 3999.toBackedInteger())
        Assert.assertEquals(BackedInteger(branchProgramContext.program.storage.read(400 * LOAD.SIZE, LOAD.SIZE)), 999.toBackedInteger())
        Assert.assertEquals(branchProgramContext.stack.size(), 4)
        Assert.assertEquals(branchProgramContext.stack.peek(), 4.toBackedInteger())
    }

    companion object {
        val SETUP_INSTRUCTIONS = with(InstructionsBuilder()) {
            save(MEMORY, push(2000.toBackedInteger()), push(200.toBackedInteger()))
            save(MEMORY, push(3000.toBackedInteger()), push(300.toBackedInteger()))
            save(DISK, push(2000.toBackedInteger()), push(200.toBackedInteger()))
            save(DISK, push(3000.toBackedInteger()), push(300.toBackedInteger()))
            push(5.toBackedInteger())
            push(4.toBackedInteger())
            push(3.toBackedInteger())
            push(2.toBackedInteger())
            push(1.toBackedInteger())
            halt(ProgramException.Reason.WINNER)
            build()
        }
        private val OVERWRITE_INSTRUCTIONS = with(InstructionsBuilder()) {
            save(MEMORY, add(load(MEMORY, push(300.toBackedInteger())), push(999.toBackedInteger())), push(300.toBackedInteger()))
            save(MEMORY, push(999.toBackedInteger()), push(400.toBackedInteger()))
            save(DISK, add(load(DISK, push(300.toBackedInteger())), push(999.toBackedInteger())), push(300.toBackedInteger()))
            save(DISK, push(999.toBackedInteger()), push(400.toBackedInteger()))
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