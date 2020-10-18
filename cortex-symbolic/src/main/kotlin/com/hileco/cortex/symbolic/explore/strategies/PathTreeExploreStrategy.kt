package com.hileco.cortex.symbolic.explore.strategies

import com.hileco.cortex.collections.layer.StackLayer
import com.hileco.cortex.symbolic.Solution
import com.hileco.cortex.symbolic.Solver
import com.hileco.cortex.symbolic.expressions.Expression.False
import com.hileco.cortex.symbolic.vm.SymbolicPathEntry
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import com.hileco.cortex.vm.ProgramException
import java.util.*

class PathTreeExploreStrategy : ExploreStrategy() {
    private val paths = Collections.synchronizedList(arrayListOf<StackLayer<SymbolicPathEntry>>())

    override fun handleComplete(symbolicVirtualMachine: SymbolicVirtualMachine) {
        if (symbolicVirtualMachine.exitedReason == ProgramException.Reason.WINNER) {
            paths.add(symbolicVirtualMachine.path.edge)
        }
    }

    override fun handleException(virtualMachine: SymbolicVirtualMachine, exception: Exception) {
        exception.printStackTrace()
    }

    override fun solve(): Solution {
        if (paths.isEmpty()) {
            return Solution(condition = False)
        }
        val pathTreeConditionBuilder = PathTreeConditionBuilder()
        val condition = pathTreeConditionBuilder.build(paths)
        val solver = Solver()
        return solver.solve(condition)
    }
}
