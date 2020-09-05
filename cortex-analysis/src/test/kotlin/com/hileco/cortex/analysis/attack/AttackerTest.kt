package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.attack.Attacker.Companion.CONSTRAINT_CALL_ADDRESS
import com.hileco.cortex.analysis.attack.Attacker.Companion.TARGET_IS_CALL
import com.hileco.cortex.analysis.attack.Attacker.Companion.TARGET_IS_HALT_WINNER
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.symbolic.Solution
import com.hileco.cortex.symbolic.expressions.Expression.Variable
import com.hileco.cortex.vm.barrier.BarrierProgram
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_00
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_02
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_03
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_06
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.Instruction
import org.junit.Assert
import org.junit.Test

class AttackerTest {
    private fun attackBarrier(barrierProgram: BarrierProgram,
                              targetPredicate: (Instruction) -> Boolean = TARGET_IS_HALT_WINNER,
                              stackConstraints: List<StackConstraint> = listOf(),
                              attackName: String = "TARGET_IS_HALT_WINNER"): Solution {
        val graph = GraphBuilder.OPTIMIZED_GRAPH_BUILDER.build(barrierProgram.instructions)
        val attacker = Attacker(targetPredicate, stackConstraints)
        val solutions = attacker.solve(graph)
        Assert.assertNotEquals(0, solutions.size)
        Assert.assertTrue(solutions.first().solvable)
        val solution = solutions.first()
        Documentation.of(Attacker::class.java.simpleName)
                .headingParagraph("Solving ${barrierProgram.name}")
                .paragraph("Program:").source(barrierProgram.pseudocode)
                .paragraph("Attack method:").source(attackName)
                .paragraph("Suggested solution by Cortex:").source(solution)
        return solution
    }

    @Test
    fun testBarrier00() {
        attackBarrier(BARRIER_00)
    }

    @Test
    fun testBarrier01() {
        val solution = attackBarrier(BARRIER_01)
        Assert.assertEquals(1, solution.values.size)
        val entry = solution.values.entries.first()
        Assert.assertTrue(entry.key is Variable)
        Assert.assertEquals(24690.toBackedInteger(), BackedInteger(entry.value.sliceArray(IntRange(1, 32))))
    }

    @Test
    fun testBarrier02() {
        attackBarrier(BARRIER_02)
    }

    @Test
    fun testBarrier03() {
        attackBarrier(BARRIER_03)
    }

    @Test
    fun testBarrier06() {
        attackBarrier(BARRIER_06, TARGET_IS_CALL, listOf(CONSTRAINT_CALL_ADDRESS(1234.toBackedInteger())), "TARGET_IS_CALL WITH CONSTRAINT_CALL_ADDRESS(1234)")
    }
}