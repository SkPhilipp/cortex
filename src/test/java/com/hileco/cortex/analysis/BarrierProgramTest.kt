package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_00
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_02
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_03
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_04
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_05
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_06
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import org.junit.Test
import java.math.BigInteger

class BarrierProgramTest {
    private fun documentBarrier(index: Int, barrierProgram: BarrierProgram) {
        val basicGraph = GraphBuilder.BASIC_GRAPH_BUILDER.build(barrierProgram.instructions)
        val basicGraphVisualized = VisualGraph()
        basicGraphVisualized.map(basicGraph)
        val optimizedGraph = GraphBuilder.OPTIMIZED_GRAPH_BUILDER.build(barrierProgram.instructions)
        val optimizedGraphVisualized = VisualGraph()
        optimizedGraphVisualized.map(optimizedGraph)
        val formattedIndex = "$index".padStart(2, '0')
        Documentation.of(BarrierProgram::class.simpleName!!)
                .headingParagraph("Barrier $formattedIndex")
                .paragraph("Description: ${barrierProgram.description}")
                .paragraph("Pseudocode").code(barrierProgram.pseudocode)
                .paragraph("Source").source(barrierProgram.instructions)
                .paragraph("Visualization:").image(basicGraphVisualized.toBytes())
    }

    @Test
    fun documentAll() {
        BarrierProgram.BARRIERS.forEachIndexed { index, barrierProgram ->
            if (barrierProgram.instructions.isNotEmpty() && barrierProgram.pseudocode.isNotBlank()) {
                documentBarrier(index, barrierProgram)
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
        val callDataOne = BigInteger.valueOf(24690).toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier02() {
        val program = Program(BARRIER_02.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = BigInteger.valueOf(24690).toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val callDataTwo = BigInteger.valueOf(512).toByteArray()
        programContext.callData.write(2 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataTwo)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier03() {
        val program = Program(BARRIER_03.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = BigInteger.valueOf(12347).toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier04() {
        val program = Program(BARRIER_04.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = BigInteger.valueOf(6).toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier05() {
        val program = Program(BARRIER_05.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = BigInteger.valueOf(3).toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier06() {
        val program = Program(BARRIER_06.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = BigInteger.valueOf(24690).toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val callDataTwo = BigInteger.valueOf(1234).toByteArray()
        programContext.callData.write(2 * LOAD.SIZE + (LOAD.SIZE - callDataTwo.size), callDataTwo)
        val virtualMachine = VirtualMachine(programContext)
        virtualMachine.atlas[BigInteger.valueOf(1234)] = Program(listOf(HALT(WINNER)))
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }
}