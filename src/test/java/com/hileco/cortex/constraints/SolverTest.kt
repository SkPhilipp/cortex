package com.hileco.cortex.constraints

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.conditions.LESS_THAN
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.math.ADD
import com.hileco.cortex.instructions.math.MODULO
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class SolverTest {
    @Test
    fun testSolve() {
        val instructions = listOf(
                PUSH(BigInteger.valueOf(10L).toByteArray()),
                PUSH(BigInteger.valueOf(0xffffffL).toByteArray()),
                PUSH(BigInteger.valueOf(10L).toByteArray()),
                PUSH(BigInteger.valueOf(0L).toByteArray()),
                LOAD(CALL_DATA),
                ADD(),
                MODULO(),
                LESS_THAN()
        )
        val expressionGenerator = ExpressionGenerator()
        instructions.forEach { expressionGenerator.addInstruction(it) }
        val solver = Solver()
        val solution = solver.solve(expressionGenerator.currentExpression)
        val onlyValue = solution.possibleValues.values.first()
        Documentation.of(Solver::class.simpleName!!)
                .headingParagraph(Solver::class.simpleName!!)
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting expression:").source(expressionGenerator.currentExpression)
                .paragraph("Suggested solution for expression to be true:").source(solution)
        Assert.assertTrue(solution.isSolvable)
        Assert.assertTrue((10L + onlyValue) % 0xffffffL < 10)
    }
}