package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.expressions.Expression.*
import com.hileco.cortex.vm.ProgramRunner.Companion.OVERFLOW_LIMIT
import java.math.BigInteger

class ExpressionOptimizer {
    private fun optimizeAdd(unoptimizedExpression: Add): Expression {
        val left = optimize(unoptimizedExpression.left)
        val right = optimize(unoptimizedExpression.right)
        if (left is Value && right is Value) {
            // a + b == result
            val a = left.constant.toBigInteger()
            val b = right.constant.toBigInteger()
            val result = a.add(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Value(result.longValueExact())
        } else if (left is Value && left.constant == 0L) {
            // 0 + ? == ?
            return right
        } else if (right is Value && right.constant == 0L) {
            // ? + 0 == ?
            return left
        } else if (left is Add && left.left is Value && right is Value) {
            // ( a + ? ) + b == ? + result
            val a = left.left.constant.toBigInteger()
            val b = right.constant.toBigInteger()
            val result = a.add(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Add(left.right, Value(result.toLong()))
        } else if (left is Add && left.right is Value && right is Value) {
            // ( ? + a ) + b == ? + result
            val a = left.right.constant.toBigInteger()
            val b = right.constant.toBigInteger()
            val result = a.add(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Add(left.left, Value(result.toLong()))
        } else if (right is Add && right.left is Value && left is Value) {
            // a + ( b + ? ) = ? + result
            val a = left.constant.toBigInteger()
            val b = right.left.constant.toBigInteger()
            val result = a.add(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Add(right.right, Value(result.toLong()))
        } else if (right is Add && right.right is Value && left is Value) {
            // a + ( ? + b ) == ? + result
            val a = left.constant.toBigInteger()
            val b = right.right.constant.toBigInteger()
            val result = a.add(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Add(right.left, Value(result.toLong()))
        } else {
            return Add(left, right)
        }
    }

    private fun optimizeSubtract(unoptimizedExpression: Subtract): Expression {
        val left = optimize(unoptimizedExpression.left)
        val right = optimize(unoptimizedExpression.right)
        if (left is Value && right is Value) {
            return Value(left.constant - right.constant)
        } else if (right is Value && right.constant == 0L) {
            // ? - 0 == ?
            return left
        } else if (right is Value && left is Subtract && left.left is Value) {
            // ( a - ? ) - b == ( result == a - b ) - ?
            val a = left.left.constant
            val b = right.constant
            val result = a - b
            return Subtract(Value(result), left.right)
        } else if (right is Value && left is Subtract && left.right is Value) {
            // ( ? - a ) - b == ? - ( result == a + b )
            val a = left.right.constant.toBigInteger()
            val b = right.constant.toBigInteger()
            val result = a.add(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Subtract(left.left, Value(result.longValueExact()))
        } else if (left is Value && right is Subtract && right.left is Value) {
            // a - ( b - ? ) == ? - ( result == b - a )
            val a = left.constant
            val b = right.left.constant
            val result = b - a
            return Subtract(right.right, Value(result))
        } else if (left is Value && right is Subtract && right.right is Value) {
            // a - ( ? - b ) == ( result == a + b ) - ?
            val a = left.constant.toBigInteger()
            val b = right.right.constant.toBigInteger()
            val result = b.add(a).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Subtract(Value(result.toLong()), right.left)
        } else {
            return Subtract(left, right)
        }
    }

    private fun optimizeMultiply(unoptimizedExpression: Multiply): Expression {
        val left = optimize(unoptimizedExpression.left)
        val right = optimize(unoptimizedExpression.right)
        if (left is Value && right is Value) {
            // a * b
            val a = left.constant.toBigInteger()
            val b = right.constant.toBigInteger()
            val result = a.multiply(b).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Value(result.longValueExact())
        } else if (left is Value && left.constant == 0L) {
            // 0 * ?
            return Value(0L)
        } else if (right is Value && right.constant == 0L) {
            // ? * 0
            return Value(0L)
        } else if (left is Value && left.constant == 1L) {
            // 1 * ?
            return right
        } else if (right is Value && right.constant == 1L) {
            // ? * 1
            return left
        } else {
            return Multiply(left, right)
        }
    }

    private fun optimizeDivide(unoptimizedExpression: Divide): Expression {
        val left = optimize(unoptimizedExpression.left)
        val right = optimize(unoptimizedExpression.right)
        if (left is Value && right is Value) {
            val a = left.constant.toBigInteger()
            val b = right.constant.toBigInteger()
            val result = a.divide(b)
            return Value(result.longValueExact())
        } else if (right is Value && right.constant == 1L) {
            // ? / 1
            return left
        } else {
            return Divide(left, right)
        }
    }

    private fun optimizeModulo(unoptimizedExpression: Modulo): Expression {
        val left = optimize(unoptimizedExpression.left)
        val right = optimize(unoptimizedExpression.right)
        if (left is Value && right is Value) {
            return Value(left.constant % right.constant)
        } else {
            return Modulo(left, right)
        }
    }

    private fun optimizeExponent(unoptimizedExpression: Exponent): Expression {
        val left = optimize(unoptimizedExpression.left)
        val right = optimize(unoptimizedExpression.right)
        if (left is Value && right is Value) {
            // a ** b
            val a = left.constant.toBigInteger()
            val b = right.constant.toBigInteger()
            // TODO: Verify that b fits in Int
            val result = a.pow(b.toInt()).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
            return Value(result.longValueExact())
        } else if (left is Value && left.constant == 0L) {
            // 0 ** ? == 0
            return Value(0L)
        } else if (right is Value && right.constant == 0L) {
            // ? ** 0 == 1
            return left
        } else if (left is Value && left.constant == 1L) {
            // 1 ** ? == 1
            return Value(1L)
        } else if (right is Value && right.constant == 1L) {
            // ? ** 1 = ?
            return left
        } else {
            return Exponent(left, right)
        }
    }

    /**
     * Removes common parts from two given expressions, preserving them as such that [Equals] will yield the same outcome.
     *
     * Examples:
     * - `SHA3(x)` to `SHA3(y)` --> `x` to `y`.
     * - `(SHA3(x) - 1) * 5` to `(SHA3(y) - 1) * 5` --> `x` to `y`.
     * - `(SHA3(x) - 1) * z` to `(SHA3(y) - 1) * z` --> `x * z` to `y * z` (As `z == 0` could influence the outcome of [Equals]).
     */
    private fun unwrap(expression: Expression, counterpart: Expression): Pair<Expression, Expression> {
        if (expression is Add && counterpart is Add) {
            when {
                expression.left == counterpart.left -> return unwrap(expression.right, counterpart.right)
                expression.right == counterpart.left -> return unwrap(expression.left, counterpart.right)
                expression.left == counterpart.right -> return unwrap(expression.right, counterpart.left)
                expression.right == counterpart.right -> return unwrap(expression.left, counterpart.left)
                else -> return expression to counterpart
            }
        }
        if (expression is Subtract && counterpart is Subtract) {
            when {
                expression.left == counterpart.left -> return unwrap(expression.right, counterpart.right)
                expression.right == counterpart.right -> return unwrap(expression.left, counterpart.left)
                else -> return expression to counterpart
            }
        }
        if (expression is Multiply && counterpart is Multiply) {
            when {
                expression.left is Value && expression.left.constant > 0 && expression.left == counterpart.left -> return unwrap(expression.right, counterpart.right)
                expression.left is Value && expression.left.constant > 0 && expression.left == counterpart.right -> return unwrap(expression.right, counterpart.left)
                expression.right is Value && expression.right.constant > 0 && expression.right == counterpart.left -> return unwrap(expression.left, counterpart.right)
                expression.right is Value && expression.right.constant > 0 && expression.right == counterpart.right -> return unwrap(expression.left, counterpart.left)
                else -> return expression to counterpart
            }
        }
        if (expression is Divide && counterpart is Divide) {
            when {
                expression.left == counterpart.left -> return unwrap(expression.right, counterpart.right)
                expression.right is Value && expression.right.constant > 0 && expression.right == counterpart.right -> return unwrap(expression.left, counterpart.left)
                else -> return expression to counterpart
            }
        }
        if (expression is Not && counterpart is Not) {
            return unwrap(expression.input, counterpart.input)
        }
        if (expression is Hash && counterpart is Hash && expression.method == counterpart.method) {
            return unwrap(expression.input, counterpart.input)
        }
        return expression to counterpart
    }

    private fun optimizeEquals(unoptimizedExpression: Equals): Expression {
        val left = optimize(unoptimizedExpression.left)
        val right = optimize(unoptimizedExpression.right)
        if (left is Value && right is Value) {
            return if (left.constant == right.constant) True else False
        } else if (left is True && right is True) {
            return True
        } else if (left is True && right is False) {
            return False
        } else if (left is False && right is True) {
            return False
        } else if (left is False && right is False) {
            return True
        } else {
            val unwrapped = unwrap(left, right)
            return Equals(unwrapped.first, unwrapped.second)
        }
    }

    private fun optimizeGreaterThan(unoptimizedExpression: GreaterThan): Expression {
        val left = optimize(unoptimizedExpression.left)
        val right = optimize(unoptimizedExpression.right)
        if (left is Value && right is Value) {
            return if (left.constant > right.constant) True else False
        } else {
            return GreaterThan(left, right)
        }
    }

    private fun optimizeLessThan(unoptimizedExpression: LessThan): Expression {
        val left = optimize(unoptimizedExpression.left)
        val right = optimize(unoptimizedExpression.right)
        if (left is Value && right is Value) {
            return if (left.constant < right.constant) True else False
        } else {
            return LessThan(left, right)
        }
    }

    private fun optimizeIsZero(unoptimizedExpression: IsZero): Expression {
        val input = optimize(unoptimizedExpression.input)
        when (input) {
            is Value -> return if (input.constant == 0L) True else False
            is True -> return False
            is False -> return True
            else -> return IsZero(input)
        }
    }

    private fun optimizeAnd(expression: And): Expression {
        val distinctInputs = expression.inputs.distinct().map { optimize(it) }.filterNot(IS_EQUIVALENT_TRUE)
        val falseInputs = distinctInputs.count(IS_EQUIVALENT_FALSE)
        return when {
            falseInputs > 0 -> False
            distinctInputs.isEmpty() -> True
            distinctInputs.size == 1 -> distinctInputs.single()
            else -> And(distinctInputs)
        }
    }

    private fun optimizeOr(expression: Or): Expression {
        val distinctInputs = expression.inputs.distinct().map { optimize(it) }.filterNot(IS_EQUIVALENT_FALSE)
        val trueInputs = distinctInputs.count(IS_EQUIVALENT_TRUE)
        return when {
            trueInputs > 0 -> True
            distinctInputs.isEmpty() -> False
            distinctInputs.size == 1 -> distinctInputs.single()
            else -> Or(distinctInputs)
        }
    }

    fun optimize(expression: Expression): Expression {
        return when (expression) {
            is Add -> optimizeAdd(expression)
            is Subtract -> optimizeSubtract(expression)
            is Multiply -> optimizeMultiply(expression)
            is Divide -> optimizeDivide(expression)
            is Modulo -> optimizeModulo(expression)
            is Exponent -> optimizeExponent(expression)
            is Equals -> optimizeEquals(expression)
            is GreaterThan -> optimizeGreaterThan(expression)
            is LessThan -> optimizeLessThan(expression)
            is IsZero -> optimizeIsZero(expression)
            is Or -> optimizeOr(expression)
            is And -> optimizeAnd(expression)
            else -> expression
        }
    }

    companion object {
        private val IS_EQUIVALENT_TRUE: (Expression) -> Boolean = { it == True || (it is Value && it.constant > 0) }
        private val IS_EQUIVALENT_FALSE: (Expression) -> Boolean = { it == False || it == Value(0) }
    }
}
