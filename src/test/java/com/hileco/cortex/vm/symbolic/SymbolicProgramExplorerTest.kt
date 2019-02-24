package com.hileco.cortex.vm.symbolic

import com.hileco.cortex.analysis.BarrierProgram
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_00
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_02
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_03
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_04
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_05
import com.hileco.cortex.constraints.Solver
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import org.junit.Assert
import org.junit.Test

class SymbolicProgramExplorerTest {
    private fun testBarrier(barrierProgram: BarrierProgram) {
        val start = System.currentTimeMillis()
        val program = SymbolicProgram(barrierProgram.instructions)
        val programContext = SymbolicProgramContext(program)
        val virtualMachine = SymbolicVirtualMachine(programContext)
        val symbolicProgramExplorer = SymbolicProgramExplorer(virtualMachine)
        symbolicProgramExplorer.run()
        val time = System.currentTimeMillis() - start
        val winningVirtualMachine = symbolicProgramExplorer.completed.first { it.exitedReason == WINNER }
        val solver = Solver()
        val solution = solver.solve(winningVirtualMachine.condition())
        Assert.assertTrue(solution.isSolvable)
        Documentation.of(SymbolicProgramExplorer::class.simpleName!!)
                .headingParagraph("Exploring ${barrierProgram.name}")
                .paragraph("Program:").source(barrierProgram.instructions)
                .paragraph("Completed paths:").source(symbolicProgramExplorer.completed.size)
                .paragraph("Impossible paths:").source(symbolicProgramExplorer.impossible.size)
                .paragraph("Total time in milliseconds:").source("$time")
                .paragraph("Suggested solution by Cortex:").source(solution)
    }

    @Test
    fun testBarrier00() {
        testBarrier(BARRIER_00)
    }

    @Test
    fun testBarrier01() {
        testBarrier(BARRIER_01)
    }

    @Test
    fun testBarrier02() {
        testBarrier(BARRIER_02)
    }

    @Test
    fun testBarrier03() {
        testBarrier(BARRIER_03)
    }

    @Test
    fun testBarrier04() {
        testBarrier(BARRIER_04)
    }

    @Test
    fun testBarrier05() {
        testBarrier(BARRIER_05)
    }
}