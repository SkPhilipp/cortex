package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.constraints.expressions.Expression.Multiply
import com.hileco.cortex.constraints.expressions.Expression.Value
import com.hileco.cortex.vm.ProgramConstants.Companion.OVERFLOW_LIMIT
import java.math.BigInteger

class MULTIPLY : MathInstruction() {
    override fun calculate(left: BigInteger, right: BigInteger): BigInteger {
        return left.multiply(right).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
    }

    override fun calculate(left: Expression, right: Expression): Expression {
        if (left is Value && right is Value) {
            val result = calculate(left.constant.toBigInteger(), right.constant.toBigInteger())
            return Value(result.toLong())
        }
        return Multiply(left, right)
    }
}
