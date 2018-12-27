package com.hileco.cortex.instructions


import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.VirtualMachine

abstract class Instruction {
    abstract val stackAdds: List<Int>
    abstract val instructionModifiers: List<ProgramZone>
    abstract val stackParameters: List<StackParameter>
    @Throws(ProgramException::class)
    abstract fun execute(process: VirtualMachine, program: ProgramContext)

    override fun equals(other: Any?): Boolean {
        return other != null && other::class == this::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String {
        return this::class.simpleName ?: "Anonymous"
    }
}
