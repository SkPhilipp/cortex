package com.hileco.cortex.symbolic.expressions

import com.hileco.cortex.collections.toBackedInteger
import org.junit.Assert
import org.junit.Test

class ExpressionTest {
    @Test
    fun testAndContainingFalse() {
        val result = Expression.constructAnd(listOf(Expression.False))
        Assert.assertEquals(Expression.False, result)
    }

    @Test
    fun testAndContainingTrue() {
        val result = Expression.constructAnd(listOf(Expression.True, Expression.Stack(0), Expression.Stack(1)))
        Assert.assertEquals(Expression.And(listOf(Expression.Stack(0), Expression.Stack(1))), result)
    }

    @Test
    fun testAndContainingSame() {
        val result = Expression.constructAnd(listOf(Expression.Stack(0), Expression.Stack(0)))
        Assert.assertEquals(Expression.Stack(0), result)
    }

    @Test
    fun testAndContainingOnlyTrue() {
        val result = Expression.constructAnd(listOf(Expression.True, Expression.Value(123.toBackedInteger())))
        Assert.assertEquals(Expression.True, result)
    }

    @Test
    fun testAndContainingNothing() {
        val result = Expression.constructAnd(listOf())
        Assert.assertEquals(Expression.True, result)
    }
}