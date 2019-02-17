package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphNode
import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.analysis.edges.FlowMapping
import com.hileco.cortex.analysis.edges.FlowType.PROGRAM_END
import com.hileco.cortex.analysis.edges.FlowType.PROGRAM_FLOW
import com.hileco.cortex.constraints.Solution
import com.hileco.cortex.constraints.Solver
import com.hileco.cortex.constraints.expressions.ImpossibleExpressionException
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.debug.HALT
import java.util.*
import java.util.stream.IntStream

class Attacker(private val targetPredicate: (GraphNode) -> Boolean) {
    private val expressionBuilder = ExpressionBuilder()

    fun solve(graph: Graph): ArrayList<Solution> {
        val solutions = ArrayList<Solution>()
        val instructions = graph.toInstructions()
        val flowMapping = graph.edgeMapping.get(FlowMapping::class.java).first()
        val pathGenerator = PathGenerator(flowMapping)
        while (pathGenerator.currentPath().isNotEmpty()) {
            val flows = pathGenerator.currentPath()
            if (containsTarget(flows, instructions, flowMapping)) {
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

    private fun containsTarget(flows: List<Flow>, instructions: List<Instruction>, flowMapping: FlowMapping): Boolean {
        flows.forEachIndexed { index, flow ->
            if (flow.type in setOf(PROGRAM_FLOW, PROGRAM_END)) {
                val next = if (index + 1 < flows.size) flows[index + 1] else null
                val source = flow.source
                val target = next?.source ?: flow.target ?: (instructions.size - 1)
                if (IntStream.range(source, target + 1)
                                .mapToObj<GraphNode> { line -> flowMapping.nodeLineMapping[line] }
                                .anyMatch(targetPredicate)) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        val TARGET_IS_HALT_WINNER: (GraphNode) -> Boolean = {
            val instruction = it.instruction
            instruction is HALT && instruction.reason == ProgramException.Reason.WINNER
        }
    }
}
