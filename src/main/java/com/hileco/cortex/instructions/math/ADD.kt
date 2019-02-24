package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.constraints.expressions.Expression.Add
import com.hileco.cortex.constraints.expressions.Expression.Value
import com.hileco.cortex.vm.ProgramConstants.Companion.OVERFLOW_LIMIT
import java.math.BigInteger

class ADD : MathInstruction() {
    override fun calculate(left: BigInteger, right: BigInteger): BigInteger {
        return left.add(right).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
    }

    override fun calculate(left: Expression, right: Expression): Expression {
        if (left is Value && right is Value) {
            val result = calculate(left.constant.toBigInteger(), right.constant.toBigInteger())
            return Value(result.toLong())
        } else if (left is Add && right is Value) {
            if (left.left is Value) {
                val result = calculate(left.left.constant.toBigInteger(), right.constant.toBigInteger())
                return Add(left.right, Value(result.toLong()))
            } else if (left.right is Value) {
                val result = calculate(left.right.constant.toBigInteger(), right.constant.toBigInteger())
                return Add(left.left, Value(result.toLong()))
            }
        } else if (right is Add && left is Value) {
            if (right.left is Value) {
                val result = calculate(right.left.constant.toBigInteger(), left.constant.toBigInteger())
                return Add(right.right, Value(result.toLong()))
            } else if (right.right is Value) {
                val result = calculate(right.right.constant.toBigInteger(), left.constant.toBigInteger())
                return Add(right.left, Value(result.toLong()))
            }
        }
        return Add(left, right)
    }
}
