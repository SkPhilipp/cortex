package com.hileco.cortex.symbolic.explore

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.symbolic.explore.strategies.PathTreeExploreStrategy
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.vm.SymbolicProgram
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import com.hileco.cortex.vm.barrier.BarrierProgram
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_00
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_02
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_03
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_04
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_05
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_07
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_09
import org.junit.Assert
import org.junit.Test

class SymbolicProgramExplorerTest {
    private fun testBarrier(barrierProgram: BarrierProgram) {
        val start = System.currentTimeMillis()
        val program = SymbolicProgram(barrierProgram.instructions)
        barrierProgram.diskSetup.forEach { (key, value) ->
            program.storage[key] = Expression.Value(value)
        }
        val programContext = SymbolicProgramContext(program)
        val virtualMachine = SymbolicVirtualMachine(programContext)
        val strategy = PathTreeExploreStrategy()
        val symbolicProgramExplorer = SymbolicProgramExplorer(strategy)
        symbolicProgramExplorer.explore(virtualMachine)
        val solution = strategy.solve()
        val time = System.currentTimeMillis() - start
        Assert.assertTrue(solution.solvable)
        Documentation.of(SymbolicProgramExplorer::class.java.simpleName)
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