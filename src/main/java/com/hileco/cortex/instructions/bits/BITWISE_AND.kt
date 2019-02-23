package com.hileco.cortex.instructions.bits

import com.hileco.cortex.constraints.expressions.Expression
import kotlin.experimental.and

class BITWISE_AND : BitInstruction() {
    override fun innerExecute(left: Byte, right: Byte): Byte {
        return left and right
    }

    override fun innerExecute(left: Expression, right: Expression): Expression {
        return Expression.BitwiseAnd(left, right)
    }
}