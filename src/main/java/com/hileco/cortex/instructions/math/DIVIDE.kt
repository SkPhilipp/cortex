package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression
import java.math.BigInteger

class DIVIDE : MathInstruction() {
    override fun innerExecute(left: BigInteger, right: BigInteger): BigInteger {
        return left.divide(right)
    }

    override fun innerExecute(left: Expression, right: Expression): Expression {
        return Expression.Divide(left, right)
    }
}
