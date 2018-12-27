package com.hileco.cortex.instructions.debug

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.StackParameter
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.VirtualMachine

class NOOP : Instruction() {

    override val stackAdds: List<Int>
        get() = listOf()

    override val instructionModifiers: List<ProgramZone>
        get() = listOf()

    override val stackParameters: List<StackParameter>
        get() = listOf()

    override fun execute(process: VirtualMachine, program: ProgramContext) {}
}
