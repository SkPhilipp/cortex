package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.expressions.Expression.*
import com.hileco.cortex.vm.ProgramRunner.Companion.OVERFLOW_LIMIT
import java.math.BigInteger

class ExpressionOptimizer {
    private fun optimizeAdd(expression: Add): Expression {
        if (expression.left is Value && expression.right is Value) {
            // a + b == result
            val a = expression.left.constant.toBigInteger()
            val b = expression.right.constant.toBigInteger()
            val result = a.add(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Value(result.longValueExact())
        } else if (expression.left is Value && expression.left.constant == 0L) {
            // 0 + ? == ?
            return expression.right
        } else if (expression.right is Value && expression.right.constant == 0L) {
            // ? + 0 == ?
            return expression.left
        } else if (expression.left is Add && expression.left.left is Value && expression.right is Value) {
            // ( a + ? ) + b == ? + result
            val a = expression.left.left.constant.toBigInteger()
            val b = expression.right.constant.toBigInteger()
            val result = a.add(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Add(expression.left.right, Value(result.toLong()))
        } else if (expression.left is Add && expression.left.right is Value && expression.right is Value) {
            // ( ? + a ) + b == ? + result
            val a = expression.left.right.constant.toBigInteger()
            val b = expression.right.constant.toBigInteger()
            val result = a.add(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Add(expression.left.left, Value(result.toLong()))
        } else if (expression.right is Add && expression.right.left is Value && expression.left is Value) {
            // a + ( b + ? ) = ? + result
            val a = expression.left.constant.toBigInteger()
            val b = expression.right.left.constant.toBigInteger()
            val result = a.add(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Add(expression.right.right, Value(result.toLong()))
        } else if (expression.right is Add && expression.right.right is Value && expression.left is Value) {
            // a + ( ? + b ) == ? + result
            val a = expression.left.constant.toBigInteger()
            val b = expression.right.right.constant.toBigInteger()
            val result = a.add(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Add(expression.right.left, Value(result.toLong()))
        } else {
            return expression
        }
    }

    private fun optimizeSubtract(expression: Subtract): Expression {
        if (expression.left is Value && expression.right is Value) {
            return Value(expression.left.constant - expression.right.constant)
        } else if (expression.right is Value && expression.right.constant == 0L) {
            // ? - 0 == ?
            return expression.left
        } else if (expression.right is Value && expression.left is Subtract && expression.left.left is Value) {
            // ( a - ? ) - b == ( result == a - b ) - ?
            val a = expression.left.left.constant
            val b = expression.right.constant
            val result = a - b
            return Subtract(Value(result), expression.left.right)
        } else if (expression.right is Value && expression.left is Subtract && expression.left.right is Value) {
            // ( ? - a ) - b == ? - ( result == a + b )
            val a = expression.left.right.constant.toBigInteger()
            val b = expression.right.constant.toBigInteger()
            val result = a.add(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Subtract(expression.left.left, Value(result.longValueExact()))
        } else if (expression.left is Value && expression.right is Subtract && expression.right.left is Value) {
            // a - ( b - ? ) ==
            val a = expression.left.constant
            val b = expression.right.left.constant
            val result = b - a
            return Subtract(expression.right.right, Value(result))
        } else if (expression.left is Value && expression.right is Subtract && expression.right.right is Value) {
            // a - ( ? - b ) ==
            val a = expression.left.constant.toBigInteger()
            val b = expression.right.right.constant.toBigInteger()
            val result = b.add(a).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Subtract(Value(result.toLong()), expression.right.left)
        } else {
            return expression
        }
    }

    private fun optimizeMultiply(expression: Multiply): Expression {
        if (expression.left is Value && expression.right is Value) {
            // a * b
            val a = expression.left.constant.toBigInteger()
            val b = expression.right.constant.toBigInteger()
            val result = a.multiply(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Value(result.longValueExact())
        } else if (expression.left is Value && expression.left.constant == 0L) {
            // 0 * ?
            return Value(0L)
        } else if (expression.right is Value && expression.right.constant == 0L) {
            // ? * 0
            return Value(0L)
        } else if (expression.left is Value && expression.left.constant == 1L) {
            // 1 * ?
            return expression.right
        } else if (expression.right is Value && expression.right.constant == 1L) {
            // ? * 1
            return expression.left
        } else {
            return expression
        }
    }

    private fun optimizeDivide(expression: Divide): Expression {
        if (expression.left is Value && expression.right is Value) {
            val left = expression.left.constant.toBigInteger()
            val right = expression.right.constant.toBigInteger()
            val result = left.divide(right)
            return Value(result.longValueExact())
        } else if (expression.right is Value && expression.right.constant == 1L) {
            // ? / 1
            return expression.left
        } else {
            return expression
        }
    }

    private fun optimizeModulo(expression: Modulo): Expression {
        if (expression.left is Value && expression.right is Value) {
            return Value(expression.left.constant % expression.right.constant)
        } else {
            return expression
        }
    }

    private fun optimizeEquals(expression: Equals): Expression {
        if (expression.left is Value && expression.right is Value) {
            return if (expression.left.constant == expression.right.constant) True else False
        } else {
            return expression
        }
    }

    private fun optimizeGreaterThan(expression: GreaterThan): Expression {
        if (expression.left is Value && expression.right is Value) {
            return if (expression.left.constant > expression.right.constant) True else False
        } else {
            return expression
        }
    }

    private fun optimizeLessThan(expression: LessThan): Expression {
        if (expression.left is Value && expression.right is Value) {
            return if (expression.left.constant < expression.right.constant) True else False
        } else {
            return expression
        }
    }

    private fun optimizeIsZero(expression: IsZero): Expression {
        when (expression.input) {
            is Value -> return if (expression.input.constant == 0L) True else False
            is True -> return False
            is False -> return True
            else -> return expression
        }
    }

    fun optimize(expression: Expression): Expression {
        return when (expression) {
            is Add -> optimizeAdd(expression)
            is Subtract -> optimizeSubtract(expression)
            is Multiply -> optimizeMultiply(expression)
            is Divide -> optimizeDivide(expression)
            is Modulo -> optimizeModulo(expression)
            is Equals -> optimizeEquals(expression)
            is GreaterThan -> optimizeGreaterThan(expression)
            is LessThan -> optimizeLessThan(expression)
            is IsZero -> optimizeIsZero(expression)
            else -> expression
        }
    }
}
