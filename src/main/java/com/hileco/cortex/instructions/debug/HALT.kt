package com.hileco.cortex.instructions.debug

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine

class HALT(val reason: ProgramException.Reason) : Instruction() {
    @Throws(ProgramException::class)
    override fun execute(process: VirtualMachine, program: ProgramContext) {
        throw ProgramException(program, reason)
    }

    override fun toString(): String {
        return "HALT $reason"
    }
}
