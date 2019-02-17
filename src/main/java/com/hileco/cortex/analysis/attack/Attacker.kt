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
        PathGenerator(flowMapping).asSequence().forEach { path ->
            val containsTarget = PathStream(instructions, path).asSequence().any { targetPredicate(it.instruction) }
            if (containsTarget) {
                try {
                    val expression = expressionBuilder.build(instructions, path)
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
    }
}
