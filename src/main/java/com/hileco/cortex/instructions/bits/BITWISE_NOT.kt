package com.hileco.cortex.instructions.bits

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.STACK
import com.hileco.cortex.vm.VirtualMachine
import kotlin.experimental.inv

class BITWISE_NOT : Instruction() {
    override val stackAdds: List<Int>
        get() = listOf(-1)

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK)

    override val stackParameters: List<StackParameter>
        get() = listOf(INPUT)

    override fun execute(process: VirtualMachine, program: ProgramContext) {
        if (program.stack.size() < 1) {
            throw ProgramException(program, ProgramException.Reason.STACK_TOO_FEW_ELEMENTS)
        }
        val element = program.stack.pop()
        val result = ByteArray(element.size)
        for (i in result.indices) {
            result[i] = element[i].inv()
        }
        program.stack.push(result)
    }

    companion object {
        val INPUT = StackParameter("input", 0)
    }
}
