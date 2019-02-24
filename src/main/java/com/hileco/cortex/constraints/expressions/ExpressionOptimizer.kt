package com.hileco.cortex.constraints.expressions

import com.hileco.cortex.instructions.math.*

class ExpressionOptimizer {
    fun optimize(expression: Expression): Expression {
        when (expression) {
            is Expression.Add -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    val result = ADD_INSTRUCTION.calculate(expression.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                    return Expression.Value(result.toLong())
                } else if (expression.left is Expression.Add && expression.right is Expression.Value) {
                    if (expression.left.left is Expression.Value) {
                        val result = ADD_INSTRUCTION.calculate(expression.left.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                        return Expression.Add(expression.left.right, Expression.Value(result.toLong()))
                    } else if (expression.left.right is Expression.Value) {
                        val result = ADD_INSTRUCTION.calculate(expression.left.right.constant.toBigInteger(), expression.right.constant.toBigInteger())
                        return Expression.Add(expression.left.left, Expression.Value(result.toLong()))
                    }
                } else if (expression.right is Expression.Add && expression.left is Expression.Value) {
                    if (expression.right.left is Expression.Value) {
                        val result = ADD_INSTRUCTION.calculate(expression.right.left.constant.toBigInteger(), expression.left.constant.toBigInteger())
                        return Expression.Add(expression.right.right, Expression.Value(result.toLong()))
                    } else if (expression.right.right is Expression.Value) {
                        val result = ADD_INSTRUCTION.calculate(expression.right.right.constant.toBigInteger(), expression.left.constant.toBigInteger())
                        return Expression.Add(expression.right.left, Expression.Value(result.toLong()))
                    }
                }
            }
            is Expression.And -> {
                val falseInputs = expression.inputs.count(IS_EQUIVALENT_FALSE)
                val trueInputs = expression.inputs.count(IS_EQUIVALENT_TRUE)
                when {
                    falseInputs > 0 -> return Expression.False
                    trueInputs == expression.inputs.size -> return Expression.True
                    trueInputs > 0 -> return Expression.And(expression.inputs.filterNot(IS_EQUIVALENT_TRUE))
                }
            }
            is Expression.Divide -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    val result = DIDIVE_INSTRUCTION.calculate(expression.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                    return Expression.Value(result.toLong())
                }
            }
            is Expression.Equals -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    return if (expression.left.constant == expression.right.constant) Expression.True else Expression.False
                }
            }
            is Expression.GreaterThan -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    return if (expression.left.constant > expression.right.constant) Expression.True else Expression.False
                }
            }
            is Expression.IsZero -> {
                if (expression.input is Expression.Value) {
                    return if (expression.input.constant == 0L) Expression.True else Expression.False
                }
            }
            is Expression.LessThan -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    return if (expression.left.constant < expression.right.constant) Expression.True else Expression.False
                }
            }
            is Expression.Modulo -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    val result = MODULO_INSTRUCTION.calculate(expression.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                    return Expression.Value(result.toLong())
                }
            }
            is Expression.Multiply -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    val result = MULTIPLY_INSTRUCTION.calculate(expression.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                    return Expression.Value(result.toLong())
                }
            }
            is Expression.Not -> {
                when (expression.input) {
                    Expression.True -> return Expression.False
                    Expression.False -> return Expression.True
                }
            }
            is Expression.Subtract -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    val result = SUBTRACT_INSTRUCTION.calculate(expression.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                    return Expression.Value(result.toLong())
                } else if (expression.left is Expression.Add && expression.right is Expression.Value) {
                    if (expression.left.left is Expression.Value) {
                        val result = SUBTRACT_INSTRUCTION.calculate(expression.left.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                        return Expression.Add(expression.left.right, Expression.Value(result.toLong()))
                    } else if (expression.left.right is Expression.Value) {
                        val result = SUBTRACT_INSTRUCTION.calculate(expression.left.right.constant.toBigInteger(), expression.right.constant.toBigInteger())
                        return Expression.Add(expression.left.left, Expression.Value(result.toLong()))
                    }
                } else if (expression.right is Expression.Add && expression.left is Expression.Value) {
                    if (expression.right.left is Expression.Value) {
                        val result = SUBTRACT_INSTRUCTION.calculate(expression.right.left.constant.toBigInteger(), expression.left.constant.toBigInteger())
                        return Expression.Add(expression.right.right, Expression.Value(result.toLong()))
                    } else if (expression.right.right is Expression.Value) {
                        val result = SUBTRACT_INSTRUCTION.calculate(expression.right.right.constant.toBigInteger(), expression.left.constant.toBigInteger())
                        return Expression.Add(expression.right.left, Expression.Value(result.toLong()))
                    }
                }
            }
        }
        return expression
    }

    companion object {
        val IS_EQUIVALENT_TRUE: (Expression) -> Boolean = { it == Expression.True || (it is Expression.Value && it.constant >= 0) }
        val IS_EQUIVALENT_FALSE: (Expression) -> Boolean = { it == Expression.False || it == Expression.Value(0) }
        val MULTIPLY_INSTRUCTION = MULTIPLY()
        val MODULO_INSTRUCTION = MODULO()
        val DIDIVE_INSTRUCTION = DIVIDE()
        val ADD_INSTRUCTION = ADD()
        val SUBTRACT_INSTRUCTION = SUBTRACT()
    }
}