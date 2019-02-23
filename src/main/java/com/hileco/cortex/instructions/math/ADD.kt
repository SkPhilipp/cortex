package com.hileco.cortex.instructions.math

import com.hileco.cortex.vm.ProgramConstants.Companion.OVERFLOW_LIMIT
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import java.math.BigInteger

class ADD : MathInstruction() {
    override fun innerExecute(process: VirtualMachine, program: ProgramContext, left: BigInteger, right: BigInteger): BigInteger {
        return left.add(right).mod(OVERFLOW_LIMIT.add(BigInteger.ONE))
    }
}
