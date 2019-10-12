package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression.*
import org.junit.Assert
import org.junit.Test

class ExpressionOptimizerTest {

    private val expressionOptimizer = ExpressionOptimizer()

    @Test
    fun lessThanValueToValue() {
        val expression = LessThan(Value(1), Value(2))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(True, result)
    }

    @Test
    fun equalsValueToValue() {
        val expression = Equals(Value(1), Value(1))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(True, result)
    }

    @Test
    fun greaterThanValueToValue() {
        val expression = GreaterThan(Value(2), Value(1))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(True, result)
    }

    @Test
    fun isZeroValue() {
        val expression = IsZero(Value(0))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(True, result)
    }

    @Test
    fun divideValueToValue() {
        val expression = Divide(Value(6), Value(3))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Value(2), result)
    }

    @Test
    fun addValueToValue() {
        val expression = Add(Value(1), Value(1))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Value(2), result)
    }

    @Test
    fun addValueToAddVariableToValue() {
        val expression = Add(Value(1), Add(Stack(0), Value(1)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Add(Stack(0), Value(2)), result)
    }

    @Test
    fun addValueToAddValueToVariable() {
        val expression = Add(Value(1), Add(Value(1), Stack(0)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Add(Stack(0), Value(2)), result)
    }

    @Test
    fun moduloValueToValue() {
        val expression = Modulo(Value(101), Value(10))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Value(1), result)
    }

    @Test
    fun multiplyValueToValue() {
        val expression = Multiply(Value(10), Value(10))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Value(100), result)
    }

    @Test
    fun subtractValueToValue() {
        val expression = Subtract(Value(1), Value(1))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Value(0), result)
    }

    /**
     * Tests for "x - 0 == x"
     */
    @Test
    fun subtractVariableToZero() {
        val expression = Subtract(Stack(0), Value(0))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Stack(0), result)
    }

    /**
     * Tests for "2 - ( x - 1 ) == 3 - x"
     */
    @Test
    fun subtractValueToSubtractVariableToValue() {
        val expression = Subtract(Value(2), Subtract(Stack(0), Value(1)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Subtract(Value(3), Stack(0)), result)
    }

    /**
     * Tests for "2 - ( 1 - x ) == x + 1" (or rather "x - -1")
     */
    @Test
    fun symbolicValueToSubtractValuetoVariable() {
        val expression = Subtract(Value(2), Subtract(Value(1), Stack(0)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Subtract(Stack(0), Value(-1)), result)
    }

    /**
     * Tests for "( x - 2 ) - 1 == x - 3"
     */
    @Test
    fun symbolicSubtractVariableToValueToValue() {
        val expression = Subtract(Subtract(Stack(0), Value(2)), Value(1))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Subtract(Stack(0), Value(3)), result)
    }

    /**
     * Tests for "( 2 - x ) - 1 == 1 - x"
     */
    @Test
    fun symbolicSubtractValuetoVariableToValue() {
        val expression = Subtract(Subtract(Value(2), Stack(0)), Value(1))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Subtract(Value(1), Stack(0)), result)
    }

}