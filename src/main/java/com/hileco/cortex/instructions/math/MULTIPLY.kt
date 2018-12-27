package com.hileco.cortex.instructions.math

import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine
import java.math.BigInteger

class MULTIPLY : MathInstruction() {
    override fun innerExecute(process: VirtualMachine, program: ProgramContext, left: BigInteger, right: BigInteger): BigInteger {
        return left.multiply(right).mod(process.overflowLimit.add(BigInteger.ONE))
    }
}
