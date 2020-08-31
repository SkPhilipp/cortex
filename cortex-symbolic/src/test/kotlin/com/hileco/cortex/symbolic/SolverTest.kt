package com.hileco.cortex.symbolic

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.symbolic.expressions.Expression.*
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
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
    fun testSolveBasic() {
        val instructions = listOf(
                PUSH(10.toBackedInteger()),
                PUSH(ZERO_32),
                LOAD(CALL_DATA),
                LESS_THAN()
        )
        val expressionGenerator = ExpressionGenerator()
        instructions.forEach { expressionGenerator.addInstruction(it) }
        val solver = Solver()
        val solution = solver.solve(expressionGenerator.currentExpression)
        val onlyValue = solution.values.values.first()
        Assert.assertTrue(solution.solvable)
        Assert.assertTrue(onlyValue < 10.toBackedInteger())
    }

    @Test
    fun testSolve() {
        val instructions = listOf(
                PUSH(10.toBackedInteger()),
                PUSH(0x100000.toBackedInteger()),
                PUSH(10.toBackedInteger()),
                PUSH(ZERO_32),
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
        Assert.assertTrue(onlyValue < 0xffffff.toBackedInteger())
    }

    @Test
    fun testSolveUninterpretedFunction() {
        val instructions = listOf(
                PUSH(ZERO_32),
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

    // TODO: Move this to a shift-right test case
    @Test
    fun testSolveGeneratedCondition() {
        val mask = "ffffffffffffffffffffffffffffffffffffffff".toBackedInteger()
        val generatedCondition = And(listOf(
                Not(LessThan(Reference(CALL_DATA, Value("03e7".toBackedInteger())), Value("04".toBackedInteger()))),
                Not(Equals(Value("ba0bba40".toBackedInteger()), ShiftRight(Value("e0".toBackedInteger()), Reference(CALL_DATA, Value(ZERO_32))))),
                Equals(Value("d0679d34".toBackedInteger()), ShiftRight(Value("e0".toBackedInteger()), Reference(CALL_DATA, Value(ZERO_32)))),
                IsZero(LessThan(Subtract(Reference(CALL_DATA, Value("03e7".toBackedInteger())), Value("4".toBackedInteger())), Value("40".toBackedInteger()))),
                Equals(BitwiseAnd(Value(mask), BitwiseAnd(Value(mask), Reference(CALL_DATA, Value("04".toBackedInteger())))), Value("deadd00d".toBackedInteger())),
                GreaterThan(Reference(CALL_DATA, Value("24".toBackedInteger())), Value(ZERO_32))
        ))
        val solver = Solver()
        val solution = solver.solve(generatedCondition)
        Assert.assertTrue("$generatedCondition must be solvable", solution.solvable)
    }
}