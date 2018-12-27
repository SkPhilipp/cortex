package com.hileco.cortex.instructions.calls

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.RETURN_DATA_TOO_LARGE
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.*
import com.hileco.cortex.vm.VirtualMachine
import java.math.BigInteger
import java.util.*

class CALL_RETURN : Instruction() {

    override val stackAdds: List<Int>
        get() = listOf()

    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK, PROGRAM_CONTEXT, MEMORY)

    override val stackParameters: List<StackParameter>
        get() = listOf(OFFSET, SIZE)

    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        if (program.stack.size() < 2) {
            throw ProgramException(program, STACK_TOO_FEW_ELEMENTS)
        }
        val offset = BigInteger(program.stack.pop())
        val size = BigInteger(program.stack.pop())
        process.programs.pop()
        val nextContext = process.programs.peek()
        if (nextContext != null) {
            val data = program.memory.read(offset.toInt(), size.toInt())
            val wSize = nextContext.returnDataSize
            if (data.size > wSize.toInt()) {
                throw ProgramException(program, RETURN_DATA_TOO_LARGE)
            }
            val dataExpanded = Arrays.copyOf(data, wSize.toInt())
            val wOffset = nextContext.returnDataOffset
            nextContext.memory.write(wOffset.toInt(), dataExpanded, wSize.toInt())
        }
    }

    companion object {
        val OFFSET = StackParameter("size", 0)
        val SIZE = StackParameter("offset", 1)
    }
}
