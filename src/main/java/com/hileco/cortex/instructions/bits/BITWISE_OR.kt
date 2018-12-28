package com.hileco.cortex.instructions.bits

import kotlin.experimental.or

class BITWISE_OR : BitInstruction() {
    override fun innerExecute(left: Byte, right: Byte): Byte {
        return left or right
    }
}