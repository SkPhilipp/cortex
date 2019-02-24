package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.constraints.expressions.Expression.Subtract
import com.hileco.cortex.constraints.expressions.Expression.Value
import java.math.BigInteger

class SUBTRACT : MathInstruction() {
    override fun calculate(left: BigInteger, right: BigInteger): BigInteger {
        return left.subtract(right)
    }

    private fun calculateAdd(left: BigInteger, right: BigInteger): BigInteger {
        return left.add(right)
    }

    override fun calculate(left: Expression, right: Expression): Expression {
        if (left is Value && right is Value) {
            val result = calculate(left.constant.toBigInteger(), right.constant.toBigInteger())
            return Value(result.toLong())
        } else if (right is Value) {
            if (right.constant == 0L) {
                return left
            } else if (left is Subtract) {
                if (left.left is Value) {
                    val result = calculate(left.left.constant.toBigInteger(), right.constant.toBigInteger())
                    return Subtract(Value(result.toLong()), left.right)
                } else if (left.right is Value) {
                    val result = calculateAdd(left.right.constant.toBigInteger(), right.constant.toBigInteger())
                    return Subtract(left.left, Value(result.toLong()))
                }
            }
        } else if (left is Value) {
            if (left.constant == 0L) {
                return right
            } else if (right is Subtract) {
                if (right.left is Value) {
                    val result = calculate(right.left.constant.toBigInteger(), left.constant.toBigInteger())
                    return Subtract(right.right, Value(result.toLong()))
                } else if (right.right is Value) {
                    val result = calculateAdd(right.right.constant.toBigInteger(), left.constant.toBigInteger())
                    return Subtract(Value(result.toLong()), right.left)
                }
            }
        }
        return Subtract(left, right)
    }
}
