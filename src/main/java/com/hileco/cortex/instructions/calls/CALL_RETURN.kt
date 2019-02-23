package com.hileco.cortex.instructions.calls

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.RETURN_DATA_TOO_LARGE
import com.hileco.cortex.instructions.ProgramException.Reason.STACK_TOO_FEW_ELEMENTS
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.ProgramZone.*
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import java.math.BigInteger
import java.util.*

class CALL_RETURN : Instruction() {
    override val instructionModifiers: List<ProgramZone>
        get() = listOf(STACK, PROGRAM_CONTEXT, MEMORY)

    override val stackParameters: List<StackParameter>
        get() = listOf(OFFSET, SIZE)

    override fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext) {
        if (programContext.stack.size() < 2) {
            throw ProgramException(STACK_TOO_FEW_ELEMENTS)
        }
        val offset = BigInteger(programContext.stack.pop())
        val size = BigInteger(programContext.stack.pop())
        virtualMachine.programs.pop()
        if (!virtualMachine.programs.isEmpty()) {
            val nextContext = virtualMachine.programs.peek()
            val data = programContext.memory.read(offset.toInt(), size.toInt())
            val wSize = nextContext.returnDataSize
            if (data.size > wSize.toInt()) {
                throw ProgramException(RETURN_DATA_TOO_LARGE)
            }
            val dataExpanded = Arrays.copyOf(data, wSize.toInt())
            val wOffset = nextContext.returnDataOffset
            nextContext.memory.write(wOffset.toInt(), dataExpanded, wSize.toInt())
            nextContext.instructionPosition++
        }
    }

    override fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext) {
        throw UnsupportedOperationException()
    }

    companion object {
        val OFFSET = StackParameter("offset", 0)
        val SIZE = StackParameter("size", 1)
    }
}
