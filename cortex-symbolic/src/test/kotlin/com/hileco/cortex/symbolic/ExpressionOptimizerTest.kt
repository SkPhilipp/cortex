package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression.*
import com.hileco.cortex.vm.bytes.BackedInteger
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ONE_32
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.vm.bytes.toBackedInteger
import org.junit.Assert
import org.junit.Test

class ExpressionOptimizerTest {

    private val expressionOptimizer = ExpressionOptimizer()

    @Test
    fun lessThanValueToValue() {
        val expression = LessThan(Value(ONE_32), Value(2.toBackedInteger()))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(True, result)
    }

    @Test
    fun equalsValueToValue() {
        val expression = Equals(Value(ONE_32), Value(ONE_32))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(True, result)
    }

    @Test
    fun greaterThanValueToValue() {
        val expression = GreaterThan(Value(2.toBackedInteger()), Value(ONE_32))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(True, result)
    }

    @Test
    fun isZeroValue() {
        val expression = IsZero(Value(ZERO_32))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(True, result)
    }

    @Test
    fun divideValueToValue() {
        val expression = Divide(Value(6.toBackedInteger()), Value(3.toBackedInteger()))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Value(2.toBackedInteger()), result)
    }

    @Test
    fun addValueToValue() {
        val expression = Add(Value(ONE_32), Value(ONE_32))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Value(2.toBackedInteger()), result)
    }

    @Test
    fun addValueToAddVariableToValue() {
        val expression = Add(Value(ONE_32), Add(Stack(0), Value(ONE_32)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Add(Stack(0), Value(2.toBackedInteger())), result)
    }

    @Test
    fun addValueToAddValueToVariable() {
        val expression = Add(Value(ONE_32), Add(Value(ONE_32), Stack(0)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Add(Stack(0), Value(2.toBackedInteger())), result)
    }

    @Test
    fun moduloValueToValue() {
        val expression = Modulo(Value(101.toBackedInteger()), Value(10.toBackedInteger()))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Value(ONE_32), result)
    }

    @Test
    fun multiplyValueToValue() {
        val expression = Multiply(Value(10.toBackedInteger()), Value(10.toBackedInteger()))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Value(100.toBackedInteger()), result)
    }

    @Test
    fun subtractValueToValue() {
        val expression = Subtract(Value(ONE_32), Value(ONE_32))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Value(ZERO_32), result)
    }

    /**
     * Tests for "x - 0 == x"
     */
    @Test
    fun subtractVariableToZero() {
        val expression = Subtract(Stack(0), Value(ZERO_32))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Stack(0), result)
    }

    /**
     * Tests for "2 - ( x - 1 ) == 3 - x"
     */
    @Test
    fun subtractValueToSubtractVariableToValue() {
        val expression = Subtract(Value(2.toBackedInteger()), Subtract(Stack(0), Value(ONE_32)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Subtract(Value(3.toBackedInteger()), Stack(0)), result)
    }

    /**
     * Tests for "2 - ( 1 - x ) == x + 1" (or rather "x - -1")
     */
    @Test
    fun subtractValueToSubtractValueToVariable() {
        val expression = Subtract(Value(2.toBackedInteger()), Subtract(Value(ONE_32), Stack(0)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Subtract(Stack(0), Value(BackedInteger.LIMIT_32)), result)
    }

    /**
     * Tests for "( x - 2 ) - 1 == x - 3"
     */
    @Test
    fun subtractSubtractVariableToValueToValue() {
        val expression = Subtract(Subtract(Stack(0), Value(2.toBackedInteger())), Value(ONE_32))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Subtract(Stack(0), Value(3.toBackedInteger())), result)
    }

    /**
     * Tests for "( 2 - x ) - 1 == 1 - x"
     */
    @Test
    fun subtractSubtractValueToVariableToValue() {
        val expression = Subtract(Subtract(Value(2.toBackedInteger()), Stack(0)), Value(ONE_32))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Subtract(Value(ONE_32), Stack(0)), result)
    }

    @Test
    fun equalsUnwrappedHash() {
        val expression = Equals(Hash(Stack(0), "SHA3"), Hash(Stack(1), "SHA3"))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Equals(Stack(0), Stack(1)), result)
    }

    @Test
    fun equalsUnwrappedNot() {
        val expression = Equals(Not(Stack(0)), Not(Stack(1)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Equals(Stack(0), Stack(1)), result)
    }

    @Test
    fun equalsUnwrappedAddOnRightRight() {
        val expression = Equals(Add(Stack(0), Stack(2)), Add(Stack(1), Stack(2)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Equals(Stack(0), Stack(1)), result)
    }

    @Test
    fun equalsUnwrappedAddOnLeftRight() {
        val expression = Equals(Add(Stack(2), Stack(0)), Add(Stack(1), Stack(2)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Equals(Stack(0), Stack(1)), result)
    }

    @Test
    fun equalsUnwrappedAddOnRightLeft() {
        val expression = Equals(Add(Stack(0), Stack(2)), Add(Stack(2), Stack(1)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Equals(Stack(0), Stack(1)), result)
    }

    @Test
    fun equalsUnwrappedAddOnLeftLeft() {
        val expression = Equals(Add(Stack(2), Stack(0)), Add(Stack(2), Stack(1)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Equals(Stack(0), Stack(1)), result)
    }

    @Test
    fun equalsUnwrappedMultiply() {
        val expression = Equals(Multiply(Stack(0), Value(3.toBackedInteger())), Multiply(Stack(1), Value(3.toBackedInteger())))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Equals(Stack(0), Stack(1)), result)
    }

    @Test
    fun equalsUnwrappedDivide() {
        val expression = Equals(Divide(Stack(0), Value(3.toBackedInteger())), Divide(Stack(1), Value(3.toBackedInteger())))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Equals(Stack(0), Stack(1)), result)
    }

    @Test
    fun andEquivalentTrue() {
        val expression = And(listOf(Value(ONE_32), Value(2.toBackedInteger())))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(True, result)
    }

    @Test
    fun andEquivalentFalse() {
        val expression = And(listOf(Value(ONE_32), Value(ZERO_32)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(False, result)
    }

    @Test
    fun andSame() {
        val expression = And(listOf(Stack(0), Stack(0)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Stack(0), result)
    }

    @Test
    fun orEquivalentTrue() {
        val expression = Or(listOf(Value(ONE_32), Value(ZERO_32)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(True, result)
    }

    @Test
    fun orEquivalentFalse() {
        val expression = Or(listOf(Value(ZERO_32), Value(ZERO_32)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(False, result)
    }

    @Test
    fun orSame() {
        val expression = Or(listOf(Stack(0), Stack(0)))
        val result = expressionOptimizer.optimize(expression)
        Assert.assertEquals(Stack(0), result)
    }

    // TODO: potential target for fuzzing
    // TODO: inner-expression optimization checking
    // TODO: checks on equals x divide by zero and equals x multiply by zero
}