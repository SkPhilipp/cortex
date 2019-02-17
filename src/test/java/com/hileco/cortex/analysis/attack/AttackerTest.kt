package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_00
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import org.junit.Assert
import org.junit.Test

class AttackerTest {
    @Test
    fun testBarrier00() {
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(BARRIER_00.instructions)
        val attacker = Attacker(Attacker.TARGET_IS_HALT_WINNER)
        val solutions = attacker.solve(graph)
        Assert.assertEquals(1, solutions.size)
        val solution = solutions.first()
        Assert.assertTrue(solution.isSolvable)
        Assert.assertEquals(0, solution.possibleValues.size)
    }

    @Test
    fun testBarrier01() {
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(BARRIER_01.instructions)
        val attacker = Attacker(Attacker.TARGET_IS_HALT_WINNER)
        val solutions = attacker.solve(graph)
        Documentation.of(Attacker::class.simpleName!!)
                .headingParagraph(Attacker::class.simpleName!!)
                .paragraph("Program:").source(BARRIER_01.instructions)
                .paragraph("Attack method:").source("TARGET_IS_HALT_WINNER")
                .paragraph("Suggested solution by Cortex:").source(solutions)
        Assert.assertEquals(1, solutions.size)
        val solution = solutions.first()
        Assert.assertTrue(solution.isSolvable)
        Assert.assertEquals(1, solution.possibleValues.size)
        val entry = solution.possibleValues.entries.first()
        Assert.assertEquals(Expression.Reference(CALL_DATA, Expression.Value(1L)), entry.key)
        Assert.assertEquals(24690L, entry.value)
    }
}