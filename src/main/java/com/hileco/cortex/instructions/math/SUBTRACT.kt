package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.vm.ProgramConstants.Companion.UNDERFLOW_LIMIT
import java.math.BigInteger

class SUBTRACT : MathInstruction() {
    override fun innerExecute(left: BigInteger, right: BigInteger): BigInteger {
        return left.subtract(right).mod(UNDERFLOW_LIMIT.subtract(BigInteger.ONE))
    }

    override fun innerExecute(left: Expression, right: Expression): Expression {
        return Expression.Subtract(left, right)
    }
}
