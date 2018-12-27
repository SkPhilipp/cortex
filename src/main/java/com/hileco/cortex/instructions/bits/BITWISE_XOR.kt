package com.hileco.cortex.instructions.bits

import kotlin.experimental.xor

class BITWISE_XOR : BitInstruction() {
    public override fun innerExecute(left: Byte, right: Byte): Byte {
        return left xor right
    }
}