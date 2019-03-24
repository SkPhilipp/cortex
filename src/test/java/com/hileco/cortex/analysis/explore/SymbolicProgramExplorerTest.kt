package com.hileco.cortex.analysis.explore

import com.hileco.cortex.analysis.BarrierProgram
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_00
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_02
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_03
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_04
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_05
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_07
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_09
import com.hileco.cortex.constraints.Solver
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.constraints.expressions.Expression.Or
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.vm.symbolic.SymbolicProgram
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import org.junit.Assert
import org.junit.Test
import java.util.*

class SymbolicProgramExplorerTest {
    private fun testBarrier(barrierProgram: BarrierProgram) {
        val start = System.currentTimeMillis()
        val program = SymbolicProgram(barrierProgram.instructions)
        barrierProgram.setup(program)
        val programContext = SymbolicProgramContext(program)
        val virtualMachine = SymbolicVirtualMachine(programContext)
        val conditions = Collections.synchronizedList(arrayListOf<Expression>())
        val symbolicProgramExplorer = SymbolicProgramExplorer(object : SymbolicProgramExplorerHandler() {
            override fun handleComplete(symbolicVirtualMachine: SymbolicVirtualMachine) {
                if (symbolicVirtualMachine.exitedReason == WINNER) {
                    conditions.add(symbolicVirtualMachine.condition())
                }
                symbolicVirtualMachine.dispose()
            }
        })
        symbolicProgramExplorer.explore(virtualMachine)
        val time = System.currentTimeMillis() - start
        val solver = Solver()
        val solution = solver.solve(Or(conditions))
        Assert.assertTrue(solution.solvable)
        Documentation.of(SymbolicProgramExplorer::class.simpleName!!)
                .headingParagraph("Exploring ${barrierProgram.name}")
                .paragraph("Program:").source(barrierProgram.pseudocode)
                .paragraph("Total time in milliseconds:").source(time)
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

    @Test
    fun testBarrier07() {
        testBarrier(BARRIER_07)
    }

    @Test
    fun testBarrier09() {
        testBarrier(BARRIER_09)
    }
}