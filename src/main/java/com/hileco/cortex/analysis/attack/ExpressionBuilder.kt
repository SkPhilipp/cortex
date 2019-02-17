package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.constraints.ExpressionGenerator
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.jumps.JUMP_IF
import java.util.*

class ExpressionBuilder {
    fun build(instructions: List<Instruction>, path: List<Flow>): Expression {
        val expressionGenerator = ExpressionGenerator()
        val conditions = ArrayList<Expression>()
        PathStream(instructions, path).asSequence().forEach {
            if (it.instruction is JUMP_IF) {
                val target = it.nextFlow?.source ?: it.wrappingFlow.target!!
                val takeJump = target == it.line && it.nextFlow != null && it.nextFlow.type.isConditional && it.nextFlow.type.isJumping
                val expression = if (takeJump) {
                    expressionGenerator.viewExpression(JUMP_IF.CONDITION.position)
                } else {
                    Expression.Not(expressionGenerator.viewExpression(JUMP_IF.CONDITION.position))
                }
                conditions.add(expression)
            }
            expressionGenerator.addInstruction(it.instruction)
        }
        return when (conditions.size) {
            0 -> Expression.True
            1 -> conditions.first()
            else -> Expression.And(conditions)
        }
    }
}
