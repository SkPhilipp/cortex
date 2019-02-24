package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.vm.ProgramConstants.Companion.OVERFLOW_LIMIT
import java.math.BigInteger

class ADD : MathInstruction() {
    override fun calculate(left: BigInteger, right: BigInteger): BigInteger {
        return left.add(right).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
    }

    override fun calculate(left: Expression, right: Expression): Expression {
        return Expression.Add(left, right)
    }
}
