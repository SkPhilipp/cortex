package com.hileco.cortex.symbolic

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.conditions.EQUALS
import com.hileco.cortex.vm.instructions.conditions.LESS_THAN
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.math.ADD
import com.hileco.cortex.vm.instructions.math.HASH
import com.hileco.cortex.vm.instructions.math.MODULO
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class SolverTest {
    @Test
    fun testSolve() {
        val instructions = listOf(
                PUSH(10.toBackedInteger()),
                PUSH(0xffffff.toBackedInteger()),
                PUSH(10.toBackedInteger()),
                PUSH(0.toBackedInteger()),
                LOAD(CALL_DATA),
                ADD(),
                MODULO(),
                LESS_THAN()
        )
        val expressionGenerator = ExpressionGenerator()
        instructions.forEach { expressionGenerator.addInstruction(it) }
        val solver = Solver()
        val solution = solver.solve(expressionGenerator.currentExpression)
        val onlyValue = solution.values.values.first()
        Documentation.of(Solver::class.java.simpleName)
                .headingParagraph(Solver::class.java.simpleName)
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting expression:").source(expressionGenerator.currentExpression)
                .paragraph("Suggested solution for expression to be true:").source(solution)
        Assert.assertTrue(solution.solvable)
        Assert.assertTrue((10L + onlyValue) % 0xffffffL < 10)
    }

    @Test
    fun testSolveUninterpretedFunction() {
        val instructions = listOf(
                PUSH(0.toBackedInteger()),
                LOAD(CALL_DATA),
                HASH("SHA-256"),
                PUSH(10.toBackedInteger()),
                HASH("SHA-256"),
                EQUALS()
        )
        val expressionGenerator = ExpressionGenerator()
        instructions.forEach { expressionGenerator.addInstruction(it) }
        val solver = Solver()
        val solution = solver.solve(expressionGenerator.currentExpression)
        Assert.assertTrue(solution.solvable)
    }
}