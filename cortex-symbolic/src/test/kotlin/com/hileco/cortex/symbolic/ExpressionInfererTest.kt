package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression.*
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import org.junit.Assert
import org.junit.Test

class ExpressionInfererTest {

    private val expressionInferer = ExpressionInferer()

    @Test
    fun testPotentialInference() {
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
    fun testRearrangePotentialInferenceExpressionAddLeft() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Add(reference, Value(500))

        val result = expressionInferer.rearrangePotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Subtract(Value(1000), Value(500)), result.right)
    }

    @Test
    fun testRearrangePotentialInferenceExpressionAddRight() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Add(Value(500), reference)

        val result = expressionInferer.rearrangePotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Subtract(Value(1000), Value(500)), result.right)
    }

    @Test
    fun testRearrangePotentialInferenceExpressionSubtractLeft() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Subtract(reference, Value(500))

        val result = expressionInferer.rearrangePotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Add(Value(1000), Value(500)), result.right)
    }

    @Test
    fun testRearrangePotentialInferenceExpressionSubtractRight() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Subtract(Value(500), reference)

        val result = expressionInferer.rearrangePotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Subtract(Value(500), Value(1000)), result.right)
    }

    @Test
    fun testRearrangePotentialInferenceExpressionMultiplyLeft() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Multiply(reference, Value(500))

        val result = expressionInferer.rearrangePotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Divide(Value(1000), Value(500)), result.right)
    }

    @Test
    fun testRearrangePotentialInferenceExpressionMultiplyRight() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Multiply(Value(500), reference)

        val result = expressionInferer.rearrangePotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Divide(Value(1000), Value(500)), result.right)
    }

    @Test
    fun testRearrangePotentialInferenceExpressionDivideLeft() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Divide(reference, Value(500))

        val result = expressionInferer.rearrangePotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Multiply(Value(1000), Value(500)), result.right)
    }

    @Test
    fun testRearrangePotentialInferenceExpressionDivideRight() {
        val reference = Reference(CALL_DATA, Value(0))
        val left = Divide(Value(500), reference)

        val result = expressionInferer.rearrangePotentialInferenceExpression(Equals(left, Value(1000)))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Divide(Value(500), Value(1000)), result.right)
    }
}
