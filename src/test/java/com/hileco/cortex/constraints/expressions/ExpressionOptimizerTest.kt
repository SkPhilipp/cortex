package com.hileco.cortex.constraints.expressions

import com.hileco.cortex.constraints.expressions.Expression.*
import org.junit.Assert
import org.junit.Test

class ExpressionOptimizerTest {
    val expressionOptimizer = ExpressionOptimizer()

    @Test
    fun testAddValueToValue() {
        val optimized = expressionOptimizer.optimize(Add(Value(1), Value(1)))
        Assert.assertEquals(Value(2), optimized)
    }

    @Test
    fun testAddValueToAddVariableToValue() {
        val optimized = expressionOptimizer.optimize(Add(Value(1), Add(Stack(0), Value(1))))
        Assert.assertEquals(Add(Stack(0), Value(2)), optimized)
    }

    @Test
    fun testAddValueToAddValuetoVariable() {
        val optimized = expressionOptimizer.optimize(Add(Value(1), Add(Value(1), Stack(0))))
        Assert.assertEquals(Add(Stack(0), Value(2)), optimized)
    }

    @Test
    fun testAndContainingFalse() {
        val optimized = expressionOptimizer.optimize(And(listOf(False)))
        Assert.assertEquals(False, optimized)
    }

    @Test
    fun testAndContainingTrue() {
        val optimized = expressionOptimizer.optimize(And(listOf(True, Stack(0), Stack(1))))
        Assert.assertEquals(And(listOf(Stack(0), Stack(1))), optimized)
    }

    @Test
    fun testAndContainingSame() {
        val optimized = expressionOptimizer.optimize(And(listOf(Stack(0), Stack(0))))
        Assert.assertEquals(And(listOf(Stack(0))), optimized)
    }

    @Test
    fun testAndContainingOnlyTrue() {
        val optimized = expressionOptimizer.optimize(And(listOf(True, Value(123))))
        Assert.assertEquals(True, optimized)
    }

    @Test
    fun testAndContainingNothing() {
        val optimized = expressionOptimizer.optimize(And(listOf()))
        Assert.assertEquals(True, optimized)
    }

    @Test
    fun testDivideValueToValue() {
        val optimized = expressionOptimizer.optimize(Divide(Value(6), Value(3)))
        Assert.assertEquals(Value(2), optimized)
    }

    @Test
    fun testEqualsValuetoValue() {
        val optimized = expressionOptimizer.optimize(Equals(Value(1), Value(1)))
        Assert.assertEquals(True, optimized)
    }

    @Test
    fun testGreaterThanValueToValue() {
        val optimized = expressionOptimizer.optimize(GreaterThan(Value(2), Value(1)))
        Assert.assertEquals(True, optimized)
    }

    @Test
    fun testIsZeroValue() {
        val optimized = expressionOptimizer.optimize(IsZero(Value(0)))
        Assert.assertEquals(True, optimized)
    }

    @Test
    fun testLessThanValueToValue() {
        val optimized = expressionOptimizer.optimize(LessThan(Value(1), Value(2)))
        Assert.assertEquals(True, optimized)
    }

    @Test
    fun testModuloValueToValue() {
        val optimized = expressionOptimizer.optimize(Modulo(Value(101), Value(10)))
        Assert.assertEquals(Value(1), optimized)
    }

    @Test
    fun testMultiplyValueToValue() {
        val optimized = expressionOptimizer.optimize(Multiply(Value(10), Value(10)))
        Assert.assertEquals(Value(100), optimized)
    }

    @Test
    fun testNotBoolean() {
        val optimized = expressionOptimizer.optimize(Not(True))
        Assert.assertEquals(False, optimized)
    }

    @Test
    fun testSubtractValueToValue() {
        val optimized = expressionOptimizer.optimize(Subtract(Value(1), Value(1)))
        Assert.assertEquals(Value(0), optimized)
    }

    @Test
    fun testSubtractValueToSubtractVariableToValue() {
        val optimized = expressionOptimizer.optimize(Subtract(Value(1), Subtract(Stack(0), Value(1))))
        Assert.assertEquals(Subtract(Stack(0), Value(0)), optimized)
    }

    @Test
    fun testSubtractValueToSubtractValuetoVariable() {
        val optimized = expressionOptimizer.optimize(Subtract(Value(1), Subtract(Value(1), Stack(0))))
        Assert.assertEquals(Subtract(Stack(0), Value(0)), optimized)
    }
}