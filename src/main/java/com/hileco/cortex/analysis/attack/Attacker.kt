package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphNode
import com.hileco.cortex.analysis.edges.EdgeFlow
import com.hileco.cortex.analysis.edges.EdgeFlowMapping
import com.hileco.cortex.analysis.edges.EdgeFlowType.*
import com.hileco.cortex.constraints.Solution
import com.hileco.cortex.constraints.Solver
import com.hileco.cortex.constraints.expressions.ImpossibleExpressionException
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.debug.HALT
import java.util.*
import java.util.stream.IntStream

class Attacker(private val targetPredicate: (GraphNode) -> Boolean) {
    fun solve(graph: Graph): ArrayList<Solution> {
        val solutions = ArrayList<Solution>()
        val instructions = graph.toInstructions()
        graph.edgeMapping.get(EdgeFlowMapping::class.java).first().let {
            val flowIterator = FlowIterator(it)
            flowIterator.forEachRemaining { edgeFlows ->
                if (isTargeted(edgeFlows, instructions, it)) {
                    val attackPath = AttackPath(instructions, edgeFlows)
                    val solver = Solver()
                    try {
                        solutions.add(solver.solve(attackPath.toExpression()))
                    } catch (ignored: ImpossibleExpressionException) {
                    }

                }
            }
        }
        return solutions
    }

    private fun isTargeted(edgeFlows: List<EdgeFlow>, instructions: List<Instruction>, edgeFlowMapping: EdgeFlowMapping): Boolean {
        return edgeFlows.asSequence()
                .filter { edgeFlow -> edgeFlow.type in arrayOf(BLOCK_PART, BLOCK_END, END) }
                .any { edgeFlow ->
                    val source = edgeFlow.source
                    val target = edgeFlow.target ?: (instructions.size - 1)
                    IntStream.range(source!!, target + 1)
                            .mapToObj<GraphNode> { line -> edgeFlowMapping.nodeLineMapping[line] }
                            .anyMatch(targetPredicate)
                }
    }

    companion object {
        val TARGET_IS_HALT_WINNER: (GraphNode) -> Boolean = {
            val instruction = it.instruction
            instruction is HALT && instruction.reason == ProgramException.Reason.WINNER
        }
    }
}
