package com.hileco.cortex.instructions.conditions

import com.hileco.cortex.constraints.expressions.Expression
import java.math.BigInteger

class EQUALS : ConditionInstruction() {
    override fun innerExecute(left: ByteArray, right: ByteArray): Boolean {
        return BigInteger(left) == BigInteger(right)
    }

    override fun innerExecute(left: Expression, right: Expression): Expression {
        return Expression.Equals(left, right)
    }
}
