package com.hileco.cortex.analysis.explore

import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine

abstract class SymbolicProgramExplorerHandler {
    /**
     * Whether to drop the given [symbolicVirtualMachine], halting further exploration.
     */
    open fun checkDrop(symbolicVirtualMachine: SymbolicVirtualMachine): Boolean {
        return symbolicVirtualMachine.path.size() >= DEFAULT_DROP_PREDICATE_PATH_LIMIT
    }

    /**
     * Whether to stop further exploration entirely.
     */
    open fun checkStop(runtime: Long): Boolean {
        return runtime >= DEFAULT_STOP_PREDICATE_TIME_LIMIT
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

    companion object {
        private const val DEFAULT_STOP_PREDICATE_TIME_LIMIT = 2000
        private const val DEFAULT_DROP_PREDICATE_PATH_LIMIT = 50
    }
}
