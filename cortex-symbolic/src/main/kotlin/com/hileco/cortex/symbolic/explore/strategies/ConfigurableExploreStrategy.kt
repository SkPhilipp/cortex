package com.hileco.cortex.symbolic.explore.strategies

import com.hileco.cortex.collections.layer.StackLayer
import com.hileco.cortex.symbolic.Solution
import com.hileco.cortex.symbolic.vm.SymbolicPathEntry
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import java.util.*
import kotlin.collections.ArrayList

class ConfigurableExploreStrategy : ExploreStrategy() {

    private val checkDrops: MutableList<(SymbolicVirtualMachine) -> Boolean> = ArrayList()
    private val checkStops: MutableList<() -> Boolean> = ArrayList()
    private val handleDrops: MutableList<(SymbolicVirtualMachine) -> Unit> = ArrayList()
    private val handleCompletes: MutableList<(SymbolicVirtualMachine) -> Unit> = ArrayList()

    fun addCheckDrops(item: (SymbolicVirtualMachine) -> Boolean) {
        checkDrops.add(item)
    }

    fun addCheckStops(item: () -> Boolean) {
        checkStops.add(item)
    }

    fun addHandleDrops(item: (SymbolicVirtualMachine) -> Unit) {
        handleDrops.add(item)
    }

    fun addHandleCompletes(item: (SymbolicVirtualMachine) -> Unit) {
        handleCompletes.add(item)
    }

    private val paths = Collections.synchronizedList(arrayListOf<StackLayer<SymbolicPathEntry>>())

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
