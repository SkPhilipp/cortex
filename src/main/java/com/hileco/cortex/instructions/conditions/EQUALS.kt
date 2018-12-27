package com.hileco.cortex.instructions.conditions

import java.util.*

class EQUALS : ConditionInstruction() {
    public override fun innerExecute(left: ByteArray, right: ByteArray): Boolean {
        return Arrays.equals(left, right)
    }
}
