package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.BarrierProgram
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_00
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_02
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_03
import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.constraints.Solution
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import org.junit.Assert
import org.junit.Test

class AttackerTest {
    private fun attackBarrier(barrierProgram: BarrierProgram): Solution {
        val graph = GraphBuilder.OPTIMIZED_GRAPH_BUILDER.build(barrierProgram.instructions)
        val attacker = Attacker(Attacker.TARGET_IS_HALT_WINNER)
        val solutions = attacker.solve(graph)
        Assert.assertNotEquals(0, solutions.size)
        Assert.assertTrue(solutions.first().isSolvable)
        return solutions.first()
    }

    @Test
    fun attackBarrier00() {
        attackBarrier(BARRIER_00)
    }

    @Test
    fun attackBarrier01() {
        val solution = attackBarrier(BARRIER_01)
        Documentation.of(Attacker::class.simpleName!!)
                .headingParagraph(Attacker::class.simpleName!!)
                .paragraph("Program:").source(BARRIER_01.instructions)
                .paragraph("Attack method:").source("TARGET_IS_HALT_WINNER")
                .paragraph("Suggested solution by Cortex:").source(solution)
        Assert.assertEquals(1, solution.possibleValues.size)
        val entry = solution.possibleValues.entries.first()
        Assert.assertEquals(Expression.Reference(CALL_DATA, Expression.Value(1L)), entry.key)
        Assert.assertEquals(24690L, entry.value)
    }

    @Test
    fun testBarrier02() {
        attackBarrier(BARRIER_02)
    }

    @Test
    fun testBarrier03() {
        attackBarrier(BARRIER_03)
    }
}