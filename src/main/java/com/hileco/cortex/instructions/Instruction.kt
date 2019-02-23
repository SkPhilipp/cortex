package com.hileco.cortex.instructions


import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.ProgramZone
import com.hileco.cortex.vm.concrete.VirtualMachine

abstract class Instruction {
    open val stackAdds: List<Int>
        get() = listOf()

    open val instructionModifiers: List<ProgramZone>
        get() = listOf()

    open val stackParameters: List<StackParameter>
        get() = listOf()

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
