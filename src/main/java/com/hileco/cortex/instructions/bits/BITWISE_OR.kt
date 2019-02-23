package com.hileco.cortex.instructions.bits

import com.hileco.cortex.constraints.expressions.Expression
import kotlin.experimental.or

class BITWISE_OR : BitInstruction() {
    override fun innerExecute(left: Byte, right: Byte): Byte {
        return left or right
    }

    override fun innerExecute(left: Expression, right: Expression): Expression {
        throw UnsupportedOperationException()
    }
}