package com.hileco.cortex.symbolic.explore.strategies

import com.hileco.cortex.symbolic.Solution
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine

class ConfigurableExploreStrategy : ExploreStrategy() {

    private val checkDrops: MutableList<(SymbolicVirtualMachine) -> Boolean> = ArrayList()
    private val checkStops: MutableList<() -> Boolean> = ArrayList()
    private val handleDrops: MutableList<(SymbolicVirtualMachine) -> Unit> = ArrayList()
    private val handleCompletes: MutableList<(SymbolicVirtualMachine) -> Unit> = ArrayList()

    override fun checkDrop(symbolicVirtualMachine: SymbolicVirtualMachine): Boolean {
        return checkDrops.any { it(symbolicVirtualMachine) }
    }

    override fun checkStop(): Boolean {
        return checkStops.any { it() }
    }

    override fun handleDrop(symbolicVirtualMachine: SymbolicVirtualMachine) {
        handleDrops.forEach { it(symbolicVirtualMachine) }
    }

    override fun handleComplete(symbolicVirtualMachine: SymbolicVirtualMachine) {
        handleCompletes.forEach { it(symbolicVirtualMachine) }
    }

    override fun solve(): Solution {
        return Solution(mapOf(), false)
    }
}
