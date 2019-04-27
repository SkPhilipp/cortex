package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_00
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_02
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_03
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_04
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_05
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_06
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_07
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_08
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_09
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_10
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.vm.concrete.Program
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.ProgramRunner
import com.hileco.cortex.vm.concrete.VirtualMachine
import org.junit.Test

class BarrierProgramTest {
    private fun documentBarrier(barrierProgram: BarrierProgram) {
        val basicGraph = GraphBuilder.BASIC_GRAPH_BUILDER.build(barrierProgram.instructions)
        val basicGraphVisualized = VisualGraph()
        basicGraphVisualized.map(basicGraph)
        val optimizedGraph = GraphBuilder.OPTIMIZED_GRAPH_BUILDER.build(barrierProgram.instructions)
        val optimizedGraphVisualized = VisualGraph()
        optimizedGraphVisualized.map(optimizedGraph)
        val document = Documentation.of(BarrierProgram::class.simpleName!!)
        document.headingParagraph(barrierProgram.name)
                .paragraph("Description: ${barrierProgram.description}")
                .paragraph("Pseudocode").source(barrierProgram.pseudocode)
                .paragraph("Source").source(barrierProgram.instructions)
                .paragraph("Visualization:").image(basicGraphVisualized)
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
        val callDataOne = 24690.toBigInteger().toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier02() {
        val program = Program(BARRIER_02.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = 24690.toBigInteger().toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val callDataTwo = 512.toBigInteger().toByteArray()
        programContext.callData.write(2 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataTwo)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier03() {
        val program = Program(BARRIER_03.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = 12347.toBigInteger().toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier04() {
        val program = Program(BARRIER_04.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = 6.toBigInteger().toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier05() {
        val program = Program(BARRIER_05.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = 3.toBigInteger().toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier06() {
        val program = Program(BARRIER_06.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = 24690.toBigInteger().toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val callDataTwo = 1234.toBigInteger().toByteArray()
        programContext.callData.write(2 * LOAD.SIZE + (LOAD.SIZE - callDataTwo.size), callDataTwo)
        val virtualMachine = VirtualMachine(programContext)
        virtualMachine.atlas[1234.toBigInteger()] = Program(listOf(HALT(WINNER)))
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier07() {
        val program = Program(BARRIER_07.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = 24690.toBigInteger().toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val callDataTwo = 513.toBigInteger().toByteArray()
        programContext.callData.write(2 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataTwo)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier08() {
        val startTime = System.currentTimeMillis()
        val program = Program(BARRIER_08.instructions, 12345.toBigInteger())
        val programContext = ProgramContext(program)
        val callDataOne = program.address.add(startTime.toBigInteger()).toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val virtualMachine = VirtualMachine(programContext, startTime = startTime)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier09() {
        val startTime = System.currentTimeMillis()
        val program = Program(BARRIER_09.instructions)
        val programContext = ProgramContext(program)
        BARRIER_09.setup(program)
        val callDataOne = 12345.toBigInteger().toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val virtualMachine = VirtualMachine(programContext, startTime = startTime)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier10() {
        val startTime = System.currentTimeMillis()
        val program = Program(BARRIER_10.instructions)
        val programContextOne = ProgramContext(program)
        val callDataOne = 2.toBigInteger().toByteArray()
        programContextOne.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val callDataTwo = 12345.toBigInteger().toByteArray()
        programContextOne.callData.write(2 * LOAD.SIZE + (LOAD.SIZE - callDataTwo.size), callDataTwo)
        val virtualMachineOne = VirtualMachine(programContextOne, startTime = startTime)
        val programRunnerOne = ProgramRunner(virtualMachineOne)
        programRunnerOne.run()

        val programContextTwo = ProgramContext(program)
        val secondCallDataOne = 1.toBigInteger().toByteArray()
        programContextTwo.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - secondCallDataOne.size), secondCallDataOne)
        val virtualMachineTwo = VirtualMachine(programContextTwo, startTime = startTime)
        val programRunnerTwo = ProgramRunner(virtualMachineTwo)
        programRunnerTwo.run()
    }
}