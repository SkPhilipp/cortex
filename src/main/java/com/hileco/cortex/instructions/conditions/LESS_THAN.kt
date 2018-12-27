package com.hileco.cortex.instructions.conditions

import java.math.BigInteger

class LESS_THAN : ConditionInstruction() {
    public override fun innerExecute(left: ByteArray, right: ByteArray): Boolean {
        return BigInteger(left) < BigInteger(right)
    }
}
