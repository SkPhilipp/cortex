package com.hileco.cortex.instructions.conditions

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.constraints.expressions.Expression.*
import java.math.BigInteger

class EQUALS : ConditionInstruction() {
    override fun innerExecute(left: ByteArray, right: ByteArray): Boolean {
        return BigInteger(left) == BigInteger(right)
    }

    override fun innerExecute(left: Expression, right: Expression): Expression {
        if (left is Value && right is Value) {
            return if (left.constant == right.constant) True else False
        }
        return Equals(left, right)
    }
}
