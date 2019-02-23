package com.hileco.cortex.instructions.math

import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import java.math.BigInteger

class MODULO : MathInstruction() {
    override fun innerExecute(process: VirtualMachine, program: ProgramContext, left: BigInteger, right: BigInteger): BigInteger {
        return left.mod(right)
    }
}
