package com.hileco.cortex.instructions.math

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import java.math.BigInteger

class MODULO : MathInstruction() {
    override fun innerExecute(left: BigInteger, right: BigInteger): BigInteger {
        return left.mod(right)
    }

    override fun innerExecute(left: Expression, right: Expression): Expression {
        return Expression.Modulo(left, right)
    }
}
