package com.hileco.cortex.vm.barrier

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.*
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_00
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_02
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_03
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_04
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_05
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_06
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_07
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_08
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_09
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_10
import com.hileco.cortex.vm.bytes.asUInt256
import com.hileco.cortex.vm.instructions.debug.HALT
import com.hileco.cortex.vm.instructions.io.LOAD
import org.junit.Test

class BarrierProgramTest {
    private fun documentBarrier(barrierProgram: BarrierProgram) {
        val document = Documentation.of(BarrierProgram::class.java.simpleName)
        document.headingParagraph(barrierProgram.name)
                .paragraph("Description: ${barrierProgram.description}")
                .paragraph("Pseudocode").source(barrierProgram.pseudocode)
                .paragraph("Source").source(barrierProgram.instructions)
        if (barrierProgram.diskSetup.isNotEmpty()) {
            document.paragraph("Disk setup").source(barrierProgram.diskSetup)
        }
    }

    @Test
    fun documentAll() {
        BarrierProgram.BARRIERS.forEach { barrierProgram ->
            if (barrierProgram.instructions.isNotEmpty() && barrierProgram.pseudocode.isNotBlank()) {
                documentBarrier(barrierProgram)
            }
        }
    }

    @Test(expected = ProgramException::class)
    fun testBarrier00() {
        val program = Program(BARRIER_00.instructions)
        val programContext = ProgramContext(program)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier01() {
        val program = Program(BARRIER_01.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = 24690.asUInt256()
        programContext.callData.write(1 * LOAD.SIZE, callDataOne.getBackingArray())
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier02() {
        val program = Program(BARRIER_02.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = 24690.asUInt256()
        programContext.callData.write(1 * LOAD.SIZE, callDataOne.getBackingArray())
        val callDataTwo = 512.asUInt256()
        programContext.callData.write(2 * LOAD.SIZE, callDataTwo.getBackingArray())
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier03() {
        val program = Program(BARRIER_03.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = 12347.asUInt256()
        programContext.callData.write(1 * LOAD.SIZE, callDataOne.getBackingArray())
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier04() {
        val program = Program(BARRIER_04.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = 6.asUInt256()
        programContext.callData.write(1 * LOAD.SIZE, callDataOne.getBackingArray())
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier05() {
        val program = Program(BARRIER_05.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = 3.asUInt256()
        programContext.callData.write(1 * LOAD.SIZE, callDataOne.getBackingArray())
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier06() {
        val program = Program(BARRIER_06.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = 24690.asUInt256()
        programContext.callData.write(1 * LOAD.SIZE, callDataOne.getBackingArray())
        val callDataTwo = 1234.asUInt256()
        programContext.callData.write(2 * LOAD.SIZE, callDataTwo.getBackingArray())
        val virtualMachine = VirtualMachine(programContext)
        virtualMachine.atlas[1234.asUInt256()] = Program(listOf(HALT(ProgramException.Reason.WINNER)))
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier07() {
        val program = Program(BARRIER_07.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = 24690.asUInt256()
        programContext.callData.write(1 * LOAD.SIZE, callDataOne.getBackingArray())
        val callDataTwo = 513.asUInt256()
        programContext.callData.write(2 * LOAD.SIZE, callDataTwo.getBackingArray())
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier08() {
        val startTime = System.currentTimeMillis()
        val program = Program(BARRIER_08.instructions, 12345.asUInt256())
        val programContext = ProgramContext(program)
        val callDataOne = program.address + startTime.asUInt256()
        programContext.callData.write(1 * LOAD.SIZE, callDataOne.getBackingArray())
        val virtualMachine = VirtualMachine(programContext, startTime = startTime)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier09() {
        val startTime = System.currentTimeMillis()
        val program = Program(BARRIER_09.instructions)
        val programContext = ProgramContext(program)
        BARRIER_09.diskSetup.forEach { (key, value) ->
            program.storage.write(key.asUInt() * LOAD.SIZE, value.getBackingArray())
        }
        val callDataOne = 12345.asUInt256()
        programContext.callData.write(1 * LOAD.SIZE, callDataOne.getBackingArray())
        val virtualMachine = VirtualMachine(programContext, startTime = startTime)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier10() {
        val startTime = System.currentTimeMillis()
        val program = Program(BARRIER_10.instructions)
        val programContextOne = ProgramContext(program)
        val callDataOne = 2.asUInt256()
        programContextOne.callData.write(1 * LOAD.SIZE, callDataOne.getBackingArray())
        val callDataTwo = 12345.asUInt256()
        programContextOne.callData.write(2 * LOAD.SIZE, callDataTwo.getBackingArray())
        val virtualMachineOne = VirtualMachine(programContextOne, startTime = startTime)
        val programRunnerOne = ProgramRunner(virtualMachineOne)
        programRunnerOne.run()

        val programContextTwo = ProgramContext(program)
        val secondCallDataOne = 1.asUInt256()
        programContextTwo.callData.write(1 * LOAD.SIZE, secondCallDataOne.getBackingArray())
        val virtualMachineTwo = VirtualMachine(programContextTwo, startTime = startTime)
        val programRunnerTwo = ProgramRunner(virtualMachineTwo)
        programRunnerTwo.run()
    }
}