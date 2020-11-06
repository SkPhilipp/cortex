package com.hileco.cortex.symbolic

import com.hileco.cortex.collections.backed.BackedInteger
import com.hileco.cortex.collections.backed.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.collections.backed.toBackedInteger
import com.hileco.cortex.collections.deserializeBytes
import com.hileco.cortex.symbolic.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.symbolic.expressions.Expression.*
import com.hileco.cortex.symbolic.instructions.conditions.EQUALS
import com.hileco.cortex.symbolic.instructions.conditions.LESS_THAN
import com.hileco.cortex.symbolic.instructions.io.LOAD
import com.hileco.cortex.symbolic.instructions.math.ADD
import com.hileco.cortex.symbolic.instructions.math.HASH
import com.hileco.cortex.symbolic.instructions.math.MODULO
import com.hileco.cortex.symbolic.instructions.stack.PUSH
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
        Assert.assertTrue(BackedInteger(onlyValue.sliceArray(IntRange(0, 0))) < 10.toBackedInteger())
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

        Assert.assertTrue(solution.solvable)
        Assert.assertTrue(BackedInteger(onlyValue.sliceArray(IntRange(0, 2))) < 0xffffff.toBackedInteger())
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
                Not(LessThan(Variable("CALL_DATA_SIZE", 256), Value("04".toBackedInteger()))),
                Not(Equals(Value("ba0bba40".toBackedInteger()), ShiftRight(Value("e0".toBackedInteger()), VariableExtract(CALL_DATA, Value(ZERO_32))))),
                Equals(Value("d0679d34".toBackedInteger()), ShiftRight(Value("e0".toBackedInteger()), VariableExtract(CALL_DATA, Value(ZERO_32)))),
                IsZero(LessThan(Subtract(Variable("CALL_DATA_SIZE", 256), Value("04".toBackedInteger())), Value("40".toBackedInteger()))),
                Equals(BitwiseAnd(Value(mask), BitwiseAnd(Value(mask), VariableExtract(CALL_DATA, Value("04".toBackedInteger())))), Value("deadd00d".toBackedInteger())),
                GreaterThan(VariableExtract(CALL_DATA, Value("24".toBackedInteger())), Value(ZERO_32))
        ))
        val solver = Solver()
        val solution = solver.solve(generatedCondition)
        Assert.assertTrue("$generatedCondition must be solvable", solution.solvable)
    }

    @Test
    fun testVariableExtractAligned() {
        val generatedCondition = And(listOf(
                Equals(Value("01234567".toBackedInteger()), ShiftRight(Value((256 - 32).toBackedInteger()), VariableExtract(CALL_DATA, Value(0.toBackedInteger())))),
                Equals(Value("89abcdef".toBackedInteger()), ShiftRight(Value((256 - 32).toBackedInteger()), VariableExtract(CALL_DATA, Value(4.toBackedInteger()))))
        ))
        val solver = Solver()
        val solution = solver.solve(generatedCondition)
        Assert.assertTrue("$generatedCondition must be solvable", solution.solvable)
        Assert.assertEquals(1, solution.values.size)
        val value = solution.values.asSequence()
                .map { it.value.sliceArray(IntRange(0, 7)) }
                .first()
        Assert.assertArrayEquals("0123456789abcdef".deserializeBytes(), value)
    }
}