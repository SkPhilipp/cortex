package com.hileco.cortex.symbolic.explore.strategies

import com.hileco.cortex.symbolic.Solution
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import com.hileco.cortex.vm.instructions.Instruction

abstract class ExploreStrategy {
    /**
     * Whether to drop the given [symbolicVirtualMachine], halting further exploration.
     */
    open fun checkDrop(symbolicVirtualMachine: SymbolicVirtualMachine): Boolean {
        return symbolicVirtualMachine.path.size() >= DEFAULT_DROP_PREDICATE_PATH_LIMIT
    }

    /**
     * Whether to halt further exploration.
     */
    open fun checkStop(): Boolean {
        return false
    }

    /**
     * To be invoked when a [SymbolicVirtualMachine] is dropped, halting further exploration.
     */
    open fun handleDrop(symbolicVirtualMachine: SymbolicVirtualMachine) {
        symbolicVirtualMachine.close()
    }

    /**
     * To be invoked when a [SymbolicVirtualMachine] completes execution, halting further exploration.
     */
    abstract fun handleComplete(symbolicVirtualMachine: SymbolicVirtualMachine)

    /**
     * To be invoked when a [SymbolicVirtualMachine] completes execution of an instruction.
     */
    open fun handleInstruction(symbolicVirtualMachine: SymbolicVirtualMachine, instruction: Instruction) {
    }

    abstract fun solve(): Solution

    companion object {
        private const val DEFAULT_DROP_PREDICATE_PATH_LIMIT = 50
    }
}
