package com.hileco.cortex.instructions.bits

import kotlin.experimental.and

class BITWISE_AND : BitInstruction() {
    public override fun innerExecute(left: Byte, right: Byte): Byte {
        return left and right
    }
}