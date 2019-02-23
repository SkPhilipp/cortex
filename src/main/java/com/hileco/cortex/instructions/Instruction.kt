package com.hileco.cortex.instructions


import com.hileco.cortex.vm.ProgramZone
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine

abstract class Instruction {
    open val stackAdds: List<Int>
        get() = listOf()

    open val instructionModifiers: List<ProgramZone>
        get() = listOf()

    open val stackParameters: List<StackParameter>
        get() = listOf()

    abstract fun execute(virtualMachine: VirtualMachine, programContext: ProgramContext)

    abstract fun execute(virtualMachine: SymbolicVirtualMachine, programContext: SymbolicProgramContext)

    override fun equals(other: Any?): Boolean {
        return other != null && other::class == this::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String {
        return this::class.simpleName!!
    }
}
