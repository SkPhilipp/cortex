package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.vm.ProgramConstants.Companion.OVERFLOW_LIMIT
import java.math.BigInteger

class MULTIPLY : MathInstruction() {
    override fun innerExecute(left: BigInteger, right: BigInteger): BigInteger {
        return left.multiply(right).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
    }

    override fun innerExecute(left: Expression, right: Expression): Expression {
        return Expression.Multiply(left, right)
    }
}
