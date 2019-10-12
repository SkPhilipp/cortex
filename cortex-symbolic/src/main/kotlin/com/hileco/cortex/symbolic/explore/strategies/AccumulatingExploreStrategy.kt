package com.hileco.cortex.symbolic.explore.strategies

import com.hileco.cortex.symbolic.Solution
import com.hileco.cortex.symbolic.Solver
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import com.hileco.cortex.vm.ProgramException
import java.util.*

class AccumulatingExploreStrategy : ExploreStrategy() {
    private val conditions = Collections.synchronizedList(arrayListOf<Expression>())

    override fun handleComplete(symbolicVirtualMachine: SymbolicVirtualMachine) {
        if (symbolicVirtualMachine.exitedReason == ProgramException.Reason.WINNER) {
            conditions.add(symbolicVirtualMachine.condition())
        }
        symbolicVirtualMachine.close()
    }

    override fun solve(): Solution {
        val condition = Expression.constructOr(conditions)
        val solver = Solver()
        return solver.solve(condition)
    }
}
