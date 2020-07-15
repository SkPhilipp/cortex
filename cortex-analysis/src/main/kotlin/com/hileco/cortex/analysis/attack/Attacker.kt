package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.edges.FlowMapping

import com.hileco.cortex.symbolic.Solution
import com.hileco.cortex.symbolic.Solver
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.vm.ProgramException
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.calls.CALL
import com.hileco.cortex.vm.instructions.debug.HALT
import java.util.*

class Attacker(private val targetPredicate: (Instruction) -> Boolean,
               private val stackConstraints: List<StackConstraint> = listOf()) {
    private val expressionBuilder = ExpressionBuilder()

    fun solve(graph: Graph): ArrayList<Solution> {
        val solutions = ArrayList<Solution>()
        val instructions = graph.toInstructions()
        val flowMapping = graph.edgeMapping.get(FlowMapping::class.java).first()
        PathGenerator(flowMapping).asSequence().forEach { path ->
            val containsTarget = PathStream(instructions, path).asSequence().any { targetPredicate(it.instruction) }
            if (containsTarget) {
                try {
                    val expression = expressionBuilder.build(instructions, path, stackConstraints)
                    val solver = Solver()
                    println("solving expression: $expression")
                    solutions.add(solver.solve(expression))
                } catch (ignored: IllegalStateException) {
                }
            }
        }
        return solutions
    }

    companion object {
        val TARGET_IS_HALT_WINNER: (Instruction) -> Boolean = {
            it is HALT && it.reason == ProgramException.Reason.WINNER
        }
        val TARGET_IS_CALL: (Instruction) -> Boolean = {
            it is CALL
        }
        val CONSTRAINT_CALL_ADDRESS: (address: Long) -> StackConstraint = { address ->
            StackConstraint(
                    { _, _, instruction -> instruction is CALL },
                    { expression -> Expression.Equals(expression, Expression.Value(address)) },
                    CALL.RECIPIENT_ADDRESS.position
            )
        }
    }
}
