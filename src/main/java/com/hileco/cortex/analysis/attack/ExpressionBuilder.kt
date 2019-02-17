package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.constraints.ExpressionGenerator
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.jumps.JUMP_IF
import java.util.*

class ExpressionBuilder {
    fun build(instructions: List<Instruction>, path: List<Flow>, stackConstraints: List<StackConstraint>): Expression {
        val expressionGenerator = ExpressionGenerator()
        val conditions = ArrayList<Expression>()
        PathStream(instructions, path).asSequence().forEachIndexed { index, element ->
            stackConstraints.forEach { stackConstraint ->
                if (stackConstraint.applicabilityPredicate(index, element.line, element.instruction)) {
                    if (expressionGenerator.viewAllExpressions().size() <= stackConstraint.stackPosition) {
                        conditions.add(Expression.False)
                    } else {
                        val stackElementExpression = expressionGenerator.viewExpression(stackConstraint.stackPosition)
                        val constraint = stackConstraint.constraintBuilder(stackElementExpression)
                        conditions.add(constraint)
                    }
                }
            }
            if (element.instruction is JUMP_IF) {
                val target = element.nextFlow?.source ?: element.wrappingFlow.target!!
                val takeJump = target == element.line && element.nextFlow != null && element.nextFlow.type.isConditional && element.nextFlow.type.isJumping
                val expression = if (takeJump) {
                    expressionGenerator.viewExpression(JUMP_IF.CONDITION.position)
                } else {
                    Expression.Not(expressionGenerator.viewExpression(JUMP_IF.CONDITION.position))
                }
                conditions.add(expression)
            }
            expressionGenerator.addInstruction(element.instruction)
        }
        return when (conditions.size) {
            0 -> Expression.True
            1 -> conditions.first()
            else -> Expression.And(conditions)
        }
    }
}
