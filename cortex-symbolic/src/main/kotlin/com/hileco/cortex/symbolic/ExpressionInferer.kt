package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.expressions.Expression.*


class ExpressionInferer(private val expressionOptimizer: ExpressionOptimizer = ExpressionOptimizer()) {

    /**
     * Constructs a set of all [Expression.Reference] contained within the given expression,
     * whose address is a [Expression.Value].
     */
    private fun directReferences(expression: Expression): Set<Expression> {
        if (expression is Reference && expression.address is Value) {
            return setOf(expression)
        }
        return expression.subexpressions().flatMap { directReferences(it) }.toSet()
    }

    /**
     * Locates potential inference subexpressions from another expression.
     *
     * These are expressions from which we can infer that a variable is exactly one value.
     * For example from a constraint such as `1234 == CALL_DATA[0] && CALL_DATA[1] != 12345`
     * it can be inferred that `CALL_DATA[0]` is `1234`.
     */
    fun locatePotentialInferenceExpressions(constraint: Expression): List<Equals> {
        if (constraint is And) {
            return constraint.inputs.flatMap { locatePotentialInferenceExpressions(it) }.toList()
        }
        if (constraint is Equals) {
            val directReferencesLeft = directReferences(constraint.left)
            val directReferencesRight = directReferences(constraint.right)
            if (directReferencesLeft.size + directReferencesRight.size == 1) {
                return listOf(constraint)
            }
        }
        return listOf()
    }

    /**
     * Attempts to rearrange the subexpressions of the given [equals] as such that in the resulting expression the left side of the equals contains only a
     * reference.
     */
    private fun rearrangeEqualsExpression(equals: Equals): Equals {

        if (equals.left is Add && directReferences(equals.left.right).isEmpty()) {
            // A + B == C --> A == C - B
            return rearrangeEqualsExpression(Equals(equals.left.left, Subtract(equals.right, equals.left.right)))
        }
        if (equals.left is Add && directReferences(equals.left.left).isEmpty()) {
            // A + B == C --> B == C - A
            return rearrangeEqualsExpression(Equals(equals.left.right, Subtract(equals.right, equals.left.left)))
        }

        if (equals.left is Subtract && directReferences(equals.left.right).isEmpty()) {
            // A - B == C --> A == C + B
            return rearrangeEqualsExpression(Equals(equals.left.left, Add(equals.right, equals.left.right)))
        }
        if (equals.left is Subtract && directReferences(equals.left.left).isEmpty()) {
            // A - B == C --> B == A - C
            return rearrangeEqualsExpression(Equals(equals.left.right, Subtract(equals.left.left, equals.right)))
        }

        if (equals.left is Multiply && directReferences(equals.left.right).isEmpty()) {
            // A * B == C --> A == C / B
            return rearrangeEqualsExpression(Equals(equals.left.left, Divide(equals.right, equals.left.right)))
        }
        if (equals.left is Multiply && directReferences(equals.left.left).isEmpty()) {
            // A * B == C --> B == C / A
            return rearrangeEqualsExpression(Equals(equals.left.right, Divide(equals.right, equals.left.left)))
        }

        if (equals.left is Divide && directReferences(equals.left.right).isEmpty()) {
            // A / B == C --> A == C * B
            return rearrangeEqualsExpression(Equals(equals.left.left, Multiply(equals.right, equals.left.right)))
        }
        if (equals.left is Divide && directReferences(equals.left.left).isEmpty()) {
            // A / B == C --> B == A / C
            return rearrangeEqualsExpression(Equals(equals.left.right, Divide(equals.left.left, equals.right)))
        }

        return equals
    }

    /**
     * Rearranged a potential [Equals] inference, moving the direct reference to the left side of the equals and passes it through further
     * rearrangement using [rearrangeEqualsExpression], so that it can potentially result in a value inference.
     */
    fun simplifyPotentialInferenceExpression(expression: Equals): Equals {
        val directReferencesLeft = directReferences(expression.left)
        val directReferencesRight = directReferences(expression.right)
        val leftSide = directReferencesRight.isEmpty() && directReferencesLeft.size == 1
        val equals = if (leftSide) Equals(expression.left, expression.right) else Equals(expression.right, expression.left)
        return rearrangeEqualsExpression(equals)
    }

    /**
     * Infers values for references using the given constraint.
     *
     * For example a simple constraint such as `10 == CALL_DATA[0] + 4` would result in the inference that `CALL_DATA[0]` is `6`.
     *
     * Note that there are inferences that can be made which will not be detected by this fairly rudimentary implementation.
     */
    fun infer(constraint: Expression): List<Pair<Reference, Value>> {
        return locatePotentialInferenceExpressions(constraint).asSequence()
                .map { simplifyPotentialInferenceExpression(it) }
                .map { Equals(it.left, expressionOptimizer.optimize(it.right)) }
                .mapNotNull { if (it.left is Reference && it.right is Value) it.left to it.right else null }
                .toList()
    }
}
