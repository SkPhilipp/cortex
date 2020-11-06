package com.hileco.cortex.symbolic

import com.hileco.cortex.collections.BackedInteger
import com.hileco.cortex.collections.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.collections.deserializeBytes
import com.hileco.cortex.collections.toBackedInteger
import com.hileco.cortex.symbolic.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.symbolic.expressions.Expression.*
import org.junit.Assert
import org.junit.Test

class SolverTest {
    @Test
    fun testSolveBasic() {
        val solver = Solver()
        val expression = LessThan(
                VariableExtract(CALL_DATA, Value(ZERO_32)),
                Value(B_10)
        )

        val solution = solver.solve(expression)
        val onlyValue = solution.values.values.first()

        Assert.assertTrue(solution.solvable)
        Assert.assertTrue(BackedInteger(onlyValue.sliceArray(IntRange(0, 31))) < 10.toBackedInteger())
    }

    @Test
    fun testSolve() {
        val solver = Solver()
        val expression = LessThan(
                Modulo(Add(VariableExtract(CALL_DATA, Value(ZERO_32)), Value(B_10)), Value(B_0x100000)),
                Value(B_10)
        )

        val solution = solver.solve(expression)
        val onlyValue = solution.values.values.first()

        Assert.assertTrue(solution.solvable)
        Assert.assertTrue(BackedInteger(onlyValue.sliceArray(IntRange(0, 31))) < 0xffffff.toBackedInteger())
    }

    @Test
    fun testSolveUninterpretedFunction() {
        val solver = Solver()
        val expression = Equals(
                Hash(Value(B_10), "SHA-256"),
                Hash(VariableExtract(CALL_DATA, Value(ZERO_32)), "SHA-256")
        )
        val expressionOptimizer = ExpressionOptimizer()

        val expressionOptimized = expressionOptimizer.optimize(expression)
        val solution = solver.solve(expressionOptimized)
        val onlyValue = solution.values.values.first()

        Assert.assertTrue(solution.solvable)
        Assert.assertTrue(BackedInteger(onlyValue.sliceArray(IntRange(0, 31))) == 10.toBackedInteger())
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

    companion object {
        private val B_10 = 10.toBackedInteger()
        private val B_0x100000 = 0x100000.toBackedInteger()
    }
}