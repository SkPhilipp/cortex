package com.hileco.cortex.analysis.explore.strategies

import com.hileco.cortex.constraints.Solution
import com.hileco.cortex.constraints.Solver
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine
import java.util.*

class AccumulatingExploreStrategy : ExploreStrategy() {
    private val conditions = Collections.synchronizedList(arrayListOf<Expression>())

    override fun handleComplete(symbolicVirtualMachine: SymbolicVirtualMachine) {
        if (symbolicVirtualMachine.exitedReason == ProgramException.Reason.WINNER) {
            conditions.add(symbolicVirtualMachine.condition())
        }
        symbolicVirtualMachine.dispose()
    }

    override fun solve(): Solution {
        val condition = Expression.constructOr(conditions)
        val solver = Solver()
        return solver.solve(condition)
    }
}
