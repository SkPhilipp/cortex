package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.edges.EdgeFlow
import com.hileco.cortex.analysis.edges.EdgeFlowType.*
import com.hileco.cortex.constraints.ExpressionGenerator
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.jumps.JUMP_IF
import java.util.*

class AttackPath(private val instructions: List<Instruction>,
                 private val edgeFlows: List<EdgeFlow>) {
    fun toExpression(): Expression {
        val expressionGenerator = ExpressionGenerator()
        val conditions = ArrayList<Expression>()
        for (i in edgeFlows.indices) {
            val edgeFlow = edgeFlows[i]
            if (BLOCK_TO_END_TYPES.contains(edgeFlow.type)) {
                val edgeFlowNextAvailable = i + 1 < edgeFlows.size
                val source = edgeFlow.source!!
                val target = edgeFlow.target ?: (instructions.size - 1)
                for (j in source..target) {
                    val instruction = instructions[j]
                    if (instruction is JUMP_IF) {
                        val isLastOfBlock = target == j
                        val isNextBlockJumpIf = edgeFlowNextAvailable && INSTRUCTION_JUMP_IF == edgeFlows[i + 1].type
                        if (!(isLastOfBlock && isNextBlockJumpIf)) {
                            val expression = Expression.Not(expressionGenerator.viewExpression(JUMP_IF.CONDITION.position))
                            conditions.add(expression)
                        } else {
                            val expression = expressionGenerator.viewExpression(JUMP_IF.CONDITION.position)
                            conditions.add(expression)
                        }
                    }
                    expressionGenerator.addInstruction(instruction)
                }
            }
        }
        return Expression.And(conditions)
    }

    companion object {
        private val BLOCK_TO_END_TYPES = setOf(BLOCK_PART, BLOCK_END, END)
    }
}
