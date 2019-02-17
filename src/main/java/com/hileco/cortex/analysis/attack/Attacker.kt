package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.edges.FlowMapping
import com.hileco.cortex.constraints.Solution
import com.hileco.cortex.constraints.Solver
import com.hileco.cortex.constraints.expressions.ImpossibleExpressionException
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.debug.HALT
import java.util.*

class Attacker(private val targetPredicate: (Instruction) -> Boolean) {
    private val expressionBuilder = ExpressionBuilder()

    fun solve(graph: Graph): ArrayList<Solution> {
        val solutions = ArrayList<Solution>()
        val instructions = graph.toInstructions()
        val flowMapping = graph.edgeMapping.get(FlowMapping::class.java).first()
        val pathGenerator = PathGenerator(flowMapping)
        while (pathGenerator.currentPath().isNotEmpty()) {
            val flows = pathGenerator.currentPath()
            val containsTarget = PathStream(instructions, flows).asSequence().any { targetPredicate(it.instruction) }
            if (containsTarget) {
                val solver = Solver()
                try {
                    val expression = expressionBuilder.build(instructions, flows)
                    solutions.add(solver.solve(expression))
                } catch (ignored: ImpossibleExpressionException) {
                }
            }
            pathGenerator.next()
        }
        return solutions
    }

    companion object {
        val TARGET_IS_HALT_WINNER: (Instruction) -> Boolean = {
            it is HALT && it.reason == ProgramException.Reason.WINNER
        }
    }
}
