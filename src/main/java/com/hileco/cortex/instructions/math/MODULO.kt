package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.constraints.expressions.Expression.Modulo
import com.hileco.cortex.constraints.expressions.Expression.Value
import java.math.BigInteger

class MODULO : MathInstruction() {
    override fun calculate(left: BigInteger, right: BigInteger): BigInteger {
        return left.mod(right)
    }

    override fun calculate(left: Expression, right: Expression): Expression {
        if (left is Value && right is Value) {
            val result = calculate(left.constant.toBigInteger(), right.constant.toBigInteger())
            return Value(result.toLong())
        }
        return Modulo(left, right)
    }
}
