package com.hileco.cortex.constraints.expressions

import com.hileco.cortex.constraints.expressions.Expression.*
import com.hileco.cortex.instructions.math.*

class ExpressionOptimizer {
    fun optimize(expression: Expression): Expression {
        when (expression) {
            is Add -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    val result = ADD_INSTRUCTION.calculate(expression.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                    return Value(result.toLong())
                } else if (expression.left is Add && expression.right is Expression.Value) {
                    if (expression.left.left is Expression.Value) {
                        val result = ADD_INSTRUCTION.calculate(expression.left.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                        return Add(expression.left.right, Value(result.toLong()))
                    } else if (expression.left.right is Expression.Value) {
                        val result = ADD_INSTRUCTION.calculate(expression.left.right.constant.toBigInteger(), expression.right.constant.toBigInteger())
                        return Add(expression.left.left, Value(result.toLong()))
                    }
                } else if (expression.right is Add && expression.left is Expression.Value) {
                    if (expression.right.left is Expression.Value) {
                        val result = ADD_INSTRUCTION.calculate(expression.right.left.constant.toBigInteger(), expression.left.constant.toBigInteger())
                        return Add(expression.right.right, Value(result.toLong()))
                    } else if (expression.right.right is Expression.Value) {
                        val result = ADD_INSTRUCTION.calculate(expression.right.right.constant.toBigInteger(), expression.left.constant.toBigInteger())
                        return Add(expression.right.left, Value(result.toLong()))
                    }
                }
            }
            is And -> {
                val distinctInputs = expression.inputs.distinct()
                val falseInputs = distinctInputs.count(IS_EQUIVALENT_FALSE)
                val trueInputs = distinctInputs.count(IS_EQUIVALENT_TRUE)
                when {
                    falseInputs > 0 -> return False
                    trueInputs == distinctInputs.size -> return True
                    trueInputs > 0 -> return And(distinctInputs.filterNot(IS_EQUIVALENT_TRUE))
                    distinctInputs.size < expression.inputs.size -> return And(distinctInputs)
                }
            }
            is Divide -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    val result = DIDIVE_INSTRUCTION.calculate(expression.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                    return Value(result.toLong())
                }
            }
            is Equals -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    return if (expression.left.constant == expression.right.constant) True else False
                }
            }
            is GreaterThan -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    return if (expression.left.constant > expression.right.constant) True else False
                }
            }
            is IsZero -> {
                if (expression.input is Expression.Value) {
                    return if (expression.input.constant == 0L) True else False
                }
            }
            is LessThan -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    return if (expression.left.constant < expression.right.constant) True else False
                }
            }
            is Modulo -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    val result = MODULO_INSTRUCTION.calculate(expression.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                    return Value(result.toLong())
                }
            }
            is Multiply -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    val result = MULTIPLY_INSTRUCTION.calculate(expression.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                    return Value(result.toLong())
                }
            }
            is Not -> {
                when (expression.input) {
                    True -> return False
                    False -> return True
                }
            }
            is Subtract -> {
                if (expression.left is Expression.Value && expression.right is Expression.Value) {
                    val result = SUBTRACT_INSTRUCTION.calculate(expression.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                    return Value(result.toLong())
                } else if (expression.left is Subtract && expression.right is Expression.Value) {
                    if (expression.left.left is Expression.Value) {
                        val result = SUBTRACT_INSTRUCTION.calculate(expression.left.left.constant.toBigInteger(), expression.right.constant.toBigInteger())
                        return Subtract(expression.left.right, Value(result.toLong()))
                    } else if (expression.left.right is Expression.Value) {
                        val result = SUBTRACT_INSTRUCTION.calculate(expression.left.right.constant.toBigInteger(), expression.right.constant.toBigInteger())
                        return Subtract(expression.left.left, Value(result.toLong()))
                    }
                } else if (expression.right is Subtract && expression.left is Expression.Value) {
                    if (expression.right.left is Expression.Value) {
                        val result = SUBTRACT_INSTRUCTION.calculate(expression.right.left.constant.toBigInteger(), expression.left.constant.toBigInteger())
                        return Subtract(expression.right.right, Value(result.toLong()))
                    } else if (expression.right.right is Expression.Value) {
                        val result = SUBTRACT_INSTRUCTION.calculate(expression.right.right.constant.toBigInteger(), expression.left.constant.toBigInteger())
                        return Subtract(expression.right.left, Value(result.toLong()))
                    }
                }
            }
        }
        return expression
    }

    companion object {
        val IS_EQUIVALENT_TRUE: (Expression) -> Boolean = { it == True || (it is Expression.Value && it.constant >= 0) }
        val IS_EQUIVALENT_FALSE: (Expression) -> Boolean = { it == False || it == Value(0) }
        val MULTIPLY_INSTRUCTION = MULTIPLY()
        val MODULO_INSTRUCTION = MODULO()
        val DIDIVE_INSTRUCTION = DIVIDE()
        val ADD_INSTRUCTION = ADD()
        val SUBTRACT_INSTRUCTION = SUBTRACT()
    }
}