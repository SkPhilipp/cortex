package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression.*
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import org.junit.Assert
import org.junit.Test

class ExpressionInfererTest {

    private val expressionInferer = ExpressionInferer()

    @Test
    fun testLocatePotentialInferenceExpressions() {
        val expectedInferenceExpressions = listOf(
                Equals(Reference(CALL_DATA, Value(0L)), Value(200L)),
                Equals(Value(100L), Reference(MEMORY, Value(1L))),
                Equals(Value(300L), Reference(CALL_DATA, Reference(CALL_DATA, Value(1L)))),
                Equals(Value(500L), Add(Reference(MEMORY, Value(1L)), Value(1L)))
        )
        val additionalExpressions = listOf(
                GreaterThan(Reference(CALL_DATA, Value(1L)), Value(400L)),
                Equals(Value(100L), Reference(MEMORY, Value(1L))),
                Equals(Reference(CALL_DATA, Value(1L)), Reference(CALL_DATA, Value(2L)))
        )

        val inferenceExpressions = expressionInferer.locatePotentialInferenceExpressions(And(
                expectedInferenceExpressions.union(additionalExpressions).toList()
        ))

        Assert.assertEquals(expectedInferenceExpressions, inferenceExpressions)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionAddLeft() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Add(reference, Value(500))

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Subtract(Value(1000), Value(500)), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionAddRight() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Add(Value(500), reference)

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Subtract(Value(1000), Value(500)), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionSubtractLeft() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Subtract(reference, Value(500))

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Add(Value(1000), Value(500)), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionSubtractRight() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Subtract(Value(500), reference)

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Subtract(Value(500), Value(1000)), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionMultiplyLeft() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Multiply(reference, Value(500))

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Divide(Value(1000), Value(500)), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionMultiplyRight() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Multiply(Value(500), reference)

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Divide(Value(1000), Value(500)), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionDivideLeft() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Divide(reference, Value(500))

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Multiply(Value(1000), Value(500)), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionDivideRight() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Divide(Value(500), reference)

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Divide(Value(500), Value(1000)), result.right)
    }

    @Test
    fun testInfer() {
        val expectedInferenceExpressions = listOf(
                Equals(Reference(CALL_DATA, Value(1L)), Value(100L)),
                Equals(Reference(CALL_DATA, Value(2L)), Add(Value(100L), Value(100L))),
                Equals(Divide(Reference(CALL_DATA, Value(3L)), Value(3L)), Value(100L))
        )
        val additionalExpressions = listOf(
                GreaterThan(Reference(CALL_DATA, Value(1L)), Value(400L)),
                Equals(Reference(CALL_DATA, Value(1L)), Reference(CALL_DATA, Value(2L)))
        )

        val inferences = expressionInferer.infer(And(
                expectedInferenceExpressions.union(additionalExpressions).toList()
        ))

        Assert.assertEquals(listOf(
                Reference(CALL_DATA, Value(1L)) to Equals(Reference(CALL_DATA, Value(1L)), Value(100L)),
                Reference(CALL_DATA, Value(2L)) to Equals(Reference(CALL_DATA, Value(2L)), Value(200L)),
                Reference(CALL_DATA, Value(3L)) to Equals(Reference(CALL_DATA, Value(3L)), Value(300L))
        ), inferences)
    }

    @Test
    fun testUnoptimizableInference() {
        val right = ShiftRight(Value(-32), Reference(CALL_DATA, Value(0)))
        val left = Value(-1173636544)

        val result = expressionInferer.infer(Equals(left, right))

        Assert.assertEquals(listOf(Reference(CALL_DATA, Value(0)) to Equals(right, left)), result)
    }
}
