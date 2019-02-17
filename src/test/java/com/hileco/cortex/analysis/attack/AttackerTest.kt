package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.BarrierProgram
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_00
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_02
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_03
import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_06
import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.attack.Attacker.Companion.CONSTRAINT_CALL_ADDRESS
import com.hileco.cortex.analysis.attack.Attacker.Companion.TARGET_IS_CALL
import com.hileco.cortex.constraints.Solution
import com.hileco.cortex.constraints.expressions.Expression.Reference
import com.hileco.cortex.constraints.expressions.Expression.Value
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AttackerTest {
    private fun attackBarrier(barrierProgram: BarrierProgram,
                              targetPredicate: (Instruction) -> Boolean = Attacker.TARGET_IS_HALT_WINNER,
                              stackConstraints: List<StackConstraint> = listOf()): Solution {
        val graph = GraphBuilder.OPTIMIZED_GRAPH_BUILDER.build(barrierProgram.instructions)
        val attacker = Attacker(targetPredicate, stackConstraints)
        val solutions = attacker.solve(graph)
        Assert.assertNotEquals(0, solutions.size)
        Assert.assertTrue(solutions.first().isSolvable)
        return solutions.first()
    }

    @Test
    fun testBarrier00() {
        val solution = attackBarrier(BARRIER_00)
        Documentation.of(Attacker::class.simpleName!!)
                .headingParagraph("Solving Barrier 00")
                .paragraph("Program:").source(BARRIER_00.instructions)
                .paragraph("Attack method:").source("TARGET_IS_HALT_WINNER")
                .paragraph("Suggested solution by Cortex:").source(solution)
    }

    @Test
    fun testBarrier01() {
        val solution = attackBarrier(BARRIER_01)
        Documentation.of(Attacker::class.simpleName!!)
                .headingParagraph("Solving Barrier 01")
                .paragraph("Program:").source(BARRIER_01.instructions)
                .paragraph("Attack method:").source("TARGET_IS_HALT_WINNER")
                .paragraph("Suggested solution by Cortex:").source(solution)
        Assert.assertEquals(1, solution.possibleValues.size)
        val entry = solution.possibleValues.entries.first()
        Assert.assertEquals(Reference(CALL_DATA, Value(1L)), entry.key)
        Assert.assertEquals(24690L, entry.value)
    }

    @Test
    fun testBarrier02() {
        val solution = attackBarrier(BARRIER_02)
        Documentation.of(Attacker::class.simpleName!!)
                .headingParagraph("Solving Barrier 02")
                .paragraph("Program:").source(BARRIER_02.instructions)
                .paragraph("Attack method:").source("TARGET_IS_HALT_WINNER")
                .paragraph("Suggested solution by Cortex:").source(solution)
    }

    @Test
    fun testBarrier03() {
        val solution = attackBarrier(BARRIER_03)
        Documentation.of(Attacker::class.simpleName!!)
                .headingParagraph("Solving Barrier 03")
                .paragraph("Program:").source(BARRIER_03.instructions)
                .paragraph("Attack method:").source("TARGET_IS_HALT_WINNER")
                .paragraph("Suggested solution by Cortex:").source(solution)
    }

    @Test
    fun testBarrier06() {
        val solution = attackBarrier(BARRIER_06, TARGET_IS_CALL, listOf(CONSTRAINT_CALL_ADDRESS(1234)))
        Documentation.of(Attacker::class.simpleName!!)
                .headingParagraph("Solving Barrier 06")
                .paragraph("Program:").source(BARRIER_06.instructions)
                .paragraph("Attack method:").source("TARGET_IS_CALL WITH CONSTRAINT_CALL_ADDRESS(1234)")
                .paragraph("Suggested solution by Cortex:").source(solution)
        Assert.assertEquals(24690L, solution.possibleValues[Reference(CALL_DATA, Value(1L))])
        Assert.assertEquals(1234L, solution.possibleValues[Reference(CALL_DATA, Value(2L))])
    }
}