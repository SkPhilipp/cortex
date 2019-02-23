package com.hileco.cortex.instructions.bits

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import kotlin.experimental.inv

class BITWISE_NOT : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(INPUT)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() < 1) {
            throw ProgramException(ProgramException.Reason.STACK_TOO_FEW_ELEMENTS)
        }
        val element = programContext.stack.pop()
        val result = ByteArray(element.size)
        for (i in result.indices) {
            result[i] = element[i].inv()
        }
        programContext.stack.push(result)
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        throw UnsupportedOperationException()
    }

    companion object {
        val INPUT = StackParameter("input", 0)
    }
}
