package com.hileco.cortex.analysis.explore.strategies

import com.hileco.cortex.constraints.Solution
import com.hileco.cortex.constraints.Solver
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.vm.layer.LayeredStack
import com.hileco.cortex.vm.symbolic.SymbolicPathEntry
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import java.util.*

class PathTreeExploreStrategy : ExploreStrategy() {
    private val paths = Collections.synchronizedList(arrayListOf<LayeredStack<SymbolicPathEntry>>())

    override fun handleComplete(symbolicVirtualMachine: SymbolicVirtualMachine) {
        if (symbolicVirtualMachine.exitedReason == ProgramException.Reason.WINNER) {
            paths.add(symbolicVirtualMachine.path)
        }
    }

    override fun solve(): Solution {
        val pathTreeConditionBuilder = PathTreeConditionBuilder()
        val condition = pathTreeConditionBuilder.build(paths)
        val solver = Solver()
        return solver.solve(condition)
    }
}
