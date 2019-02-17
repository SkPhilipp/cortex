package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_00
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_02
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_03
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_04
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_05
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import org.junit.Test
import java.math.BigInteger

class BarrierProgramTest {
    @Test(expected = ProgramException::class)
    fun testBarrier00() {
        Documentation.of(BarrierProgram::class.simpleName!!)
                .headingParagraph("Barrier 00")
                .paragraph("Description: ${BARRIER_00.description}")
                .paragraph("Pseudocode").code(BARRIER_00.pseudocode)
                .paragraph("Source").source(BARRIER_00.instructions)
        val program = Program(BARRIER_00.instructions)
        val programContext = ProgramContext(program)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }

    @Test(expected = ProgramException::class)
    fun testBarrier01() {
        Documentation.of(BarrierProgram::class.simpleName!!)
                .headingParagraph("Barrier 01")
                .paragraph("Description: ${BARRIER_01.description}")
                .paragraph("Pseudocode").code(BARRIER_01.pseudocode)
                .paragraph("Source").source(BARRIER_01.instructions)
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
        Documentation.of(BarrierProgram::class.simpleName!!)
                .headingParagraph("Barrier 02")
                .paragraph("Description: ${BARRIER_02.description}")
                .paragraph("Pseudocode").code(BARRIER_02.pseudocode)
                .paragraph("Source").source(BARRIER_02.instructions)
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
        Documentation.of(BarrierProgram::class.simpleName!!)
                .headingParagraph("Barrier 03")
                .paragraph("Description: ${BARRIER_03.description}")
                .paragraph("Pseudocode").code(BARRIER_03.pseudocode)
                .paragraph("Source").source(BARRIER_03.instructions)
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
        Documentation.of(BarrierProgram::class.simpleName!!)
                .headingParagraph("Barrier 04")
                .paragraph("Description: ${BARRIER_04.description}")
                .paragraph("Pseudocode").code(BARRIER_04.pseudocode)
                .paragraph("Source").source(BARRIER_04.instructions)
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
        Documentation.of(BarrierProgram::class.simpleName!!)
                .headingParagraph("Barrier 05")
                .paragraph("Description: ${BARRIER_05.description}")
                .paragraph("Pseudocode").code(BARRIER_05.pseudocode)
                .paragraph("Source").source(BARRIER_05.instructions)
        val program = Program(BARRIER_05.instructions)
        val programContext = ProgramContext(program)
        val callDataOne = BigInteger.valueOf(3).toByteArray()
        programContext.callData.write(1 * LOAD.SIZE + (LOAD.SIZE - callDataOne.size), callDataOne)
        val virtualMachine = VirtualMachine(programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
    }
}