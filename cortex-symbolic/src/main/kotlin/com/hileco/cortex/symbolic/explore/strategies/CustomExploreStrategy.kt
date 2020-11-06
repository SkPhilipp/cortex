package com.hileco.cortex.symbolic.explore.strategies

import com.hileco.cortex.collections.StackLayer
import com.hileco.cortex.symbolic.ExpressionOptimizer
import com.hileco.cortex.symbolic.Solution
import com.hileco.cortex.symbolic.Solver
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.expressions.Expression.False
import com.hileco.cortex.symbolic.vm.SymbolicPathEntry
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import com.hileco.cortex.collections.BackedInteger.Companion.ZERO_32
import java.util.*

// TODO: Tests for PathTreeExploreStrategy & CustomExploreStrategy
class CustomExploreStrategy : ExploreStrategy() {
    private val paths = Collections.synchronizedList(arrayListOf<StackLayer<SymbolicPathEntry>>())
    private val filterCompleted: MutableList<(SymbolicVirtualMachine) -> Boolean> = mutableListOf()
    private val conditions: MutableList<(SymbolicVirtualMachine) -> Expression> = mutableListOf()
    private val expressionOptimizer = ExpressionOptimizer()
    private val exceptions: MutableList<Exception> = mutableListOf()

    override fun handleComplete(symbolicVirtualMachine: SymbolicVirtualMachine) {
        val passes = filterCompleted.any { it(symbolicVirtualMachine) }
        if (passes) {
            val conditions = conditions.map { it(symbolicVirtualMachine) }
            val condition = Expression.constructAnd(conditions)
            if (condition == False) {
                return
            }
            if (condition != Expression.True) {
                val customPath = symbolicVirtualMachine.path.copy()
                customPath.push(SymbolicPathEntry(0, Expression.Value(ZERO_32), true, condition))
                paths.add(customPath.edge)
            } else {
                paths.add(symbolicVirtualMachine.path.edge)
            }
        }
    }

    override fun handleException(virtualMachine: SymbolicVirtualMachine, exception: Exception) {
        exceptions.add(exception)
    }

    fun withCompleteFilter(entry: (SymbolicVirtualMachine) -> Boolean) {
        this.filterCompleted.add(entry)
    }

    fun withCondition(entry: (SymbolicVirtualMachine) -> Expression) {
        this.conditions.add(entry)
    }

    fun exceptions(): List<Exception> {
        return exceptions
    }

    override fun solve(): Solution {
        if (paths.isEmpty()) {
            return Solution(condition = False)
        }
        val pathTreeConditionBuilder = PathTreeConditionBuilder()
        val condition = pathTreeConditionBuilder.build(paths)
        val optimizedCondition = expressionOptimizer.optimize(condition)
        val solver = Solver()
        return solver.solve(optimizedCondition)
    }
}
