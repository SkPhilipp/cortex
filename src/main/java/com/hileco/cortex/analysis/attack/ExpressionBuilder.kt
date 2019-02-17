package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.analysis.edges.FlowType
import com.hileco.cortex.constraints.ExpressionGenerator
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.jumps.JUMP_IF
import java.util.*

class ExpressionBuilder {
    fun build(instructions: List<Instruction>, flows: List<Flow>): Expression {
        val expressionGenerator = ExpressionGenerator()
        val conditions = ArrayList<Expression>()
        flows.forEachIndexed { flowIndex, flow ->
            if (flow.type == FlowType.PROGRAM_FLOW) {
                val next = if (flowIndex + 1 < flows.size) flows[flowIndex + 1] else null
                val source = flow.source
                val target = next?.source ?: flow.target!!
                for (instructionIndex in source..target) {
                    val instruction = instructions[instructionIndex]
                    if (instruction is JUMP_IF) {
                        val takeJump = target == instructionIndex && next?.type == FlowType.INSTRUCTION_JUMP_IF
                        val expression = if (takeJump) expressionGenerator.viewExpression(JUMP_IF.CONDITION.position) else Expression.Not(expressionGenerator.viewExpression(JUMP_IF.CONDITION.position))
                        conditions.add(expression)
                    }
                    expressionGenerator.addInstruction(instruction)
                }
            }
        }
        return when (conditions.size) {
            0 -> Expression.True
            1 -> conditions.first()
            else -> Expression.And(conditions)
        }
    }
}
