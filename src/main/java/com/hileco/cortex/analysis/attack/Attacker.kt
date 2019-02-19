package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.edges.FlowMapping
import com.hileco.cortex.constraints.Solution
import com.hileco.cortex.constraints.Solver
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.constraints.expressions.ImpossibleExpressionException
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.calls.CALL
import com.hileco.cortex.instructions.debug.HALT
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
                    solutions.add(solver.solve(expression))
                } catch (ignored: ImpossibleExpressionException) {
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
