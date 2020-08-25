package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression.*
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import com.hileco.cortex.vm.bytes.toBackedInteger
import org.junit.Assert
import org.junit.Test

class ExpressionInfererTest {

    private val expressionInferer = ExpressionInferer()

    @Test
    fun testLocatePotentialInferenceExpressions() {
        val expectedInferenceExpressions = listOf(
                Equals(Reference(CALL_DATA, Value(0.toBackedInteger())), Value(200.toBackedInteger())),
                Equals(Value(100.toBackedInteger()), Reference(MEMORY, Value(1.toBackedInteger()))),
                Equals(Value(300.toBackedInteger()), Reference(CALL_DATA, Reference(CALL_DATA, Value(1.toBackedInteger())))),
                Equals(Value(500.toBackedInteger()), Add(Reference(MEMORY, Value(1.toBackedInteger())), Value(1.toBackedInteger())))
        )
        val additionalExpressions = listOf(
                GreaterThan(Reference(CALL_DATA, Value(1.toBackedInteger())), Value(400.toBackedInteger())),
                Equals(Value(100.toBackedInteger()), Reference(MEMORY, Value(1.toBackedInteger()))),
                Equals(Reference(CALL_DATA, Value(1.toBackedInteger())), Reference(CALL_DATA, Value(2.toBackedInteger())))
        )

        val inferenceExpressions = expressionInferer.locatePotentialInferenceExpressions(And(
                expectedInferenceExpressions.union(additionalExpressions).toList()
        ))

        Assert.assertEquals(expectedInferenceExpressions, inferenceExpressions)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionAddLeft() {
        val reference = Reference(CALL_DATA, Value(0.toBackedInteger()))
        val left = Add(reference, Value(500.toBackedInteger()))

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000.toBackedInteger())))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Subtract(Value(1000.toBackedInteger()), Value(500.toBackedInteger())), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionAddRight() {
        val reference = Reference(CALL_DATA, Value(0.toBackedInteger()))
        val left = Add(Value(500.toBackedInteger()), reference)

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000.toBackedInteger())))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Subtract(Value(1000.toBackedInteger()), Value(500.toBackedInteger())), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionSubtractLeft() {
        val reference = Reference(CALL_DATA, Value(0.toBackedInteger()))
        val left = Subtract(reference, Value(500.toBackedInteger()))

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000.toBackedInteger())))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Add(Value(1000.toBackedInteger()), Value(500.toBackedInteger())), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionSubtractRight() {
        val reference = Reference(CALL_DATA, Value(0.toBackedInteger()))
        val left = Subtract(Value(500.toBackedInteger()), reference)

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000.toBackedInteger())))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Subtract(Value(500.toBackedInteger()), Value(1000.toBackedInteger())), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionMultiplyLeft() {
        val reference = Reference(CALL_DATA, Value(0.toBackedInteger()))
        val left = Multiply(reference, Value(500.toBackedInteger()))

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000.toBackedInteger())))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Divide(Value(1000.toBackedInteger()), Value(500.toBackedInteger())), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionMultiplyRight() {
        val reference = Reference(CALL_DATA, Value(0.toBackedInteger()))
        val left = Multiply(Value(500.toBackedInteger()), reference)

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000.toBackedInteger())))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Divide(Value(1000.toBackedInteger()), Value(500.toBackedInteger())), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionDivideLeft() {
        val reference = Reference(CALL_DATA, Value(0.toBackedInteger()))
        val left = Divide(reference, Value(500.toBackedInteger()))

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000.toBackedInteger())))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Multiply(Value(1000.toBackedInteger()), Value(500.toBackedInteger())), result.right)
    }

    @Test
    fun testSimplifyPotentialInferenceExpressionDivideRight() {
        val reference = Reference(CALL_DATA, Value(0.toBackedInteger()))
        val left = Divide(Value(500.toBackedInteger()), reference)

        val result = expressionInferer.simplifyPotentialInferenceExpression(Equals(left, Value(1000.toBackedInteger())))

        Assert.assertEquals(reference, result.left)
        Assert.assertEquals(Divide(Value(500.toBackedInteger()), Value(1000.toBackedInteger())), result.right)
    }

    @Test
    fun testInfer() {
        val expectedInferenceExpressions = listOf(
                Equals(Reference(CALL_DATA, Value(1.toBackedInteger())), Value(100.toBackedInteger())),
                Equals(Reference(CALL_DATA, Value(2.toBackedInteger())), Add(Value(100.toBackedInteger()), Value(100.toBackedInteger()))),
                Equals(Divide(Reference(CALL_DATA, Value(3.toBackedInteger())), Value(3.toBackedInteger())), Value(100.toBackedInteger()))
        )
        val additionalExpressions = listOf(
                GreaterThan(Reference(CALL_DATA, Value(1.toBackedInteger())), Value(400.toBackedInteger())),
                Equals(Reference(CALL_DATA, Value(1.toBackedInteger())), Reference(CALL_DATA, Value(2.toBackedInteger())))
        )

        val inferences = expressionInferer.infer(And(
                expectedInferenceExpressions.union(additionalExpressions).toList()
        ))

        Assert.assertEquals(listOf(
                Reference(CALL_DATA, Value(1.toBackedInteger())) to Equals(Reference(CALL_DATA, Value(1.toBackedInteger())), Value(100.toBackedInteger())),
                Reference(CALL_DATA, Value(2.toBackedInteger())) to Equals(Reference(CALL_DATA, Value(2.toBackedInteger())), Value(200.toBackedInteger())),
                Reference(CALL_DATA, Value(3.toBackedInteger())) to Equals(Reference(CALL_DATA, Value(3.toBackedInteger())), Value(300.toBackedInteger()))
        ), inferences)
    }

    @Test
    fun testUnoptimizableInference() {
         val right = ShiftRight(Value((32).toBackedInteger()), Reference(CALL_DATA, Value(0.toBackedInteger())))
        val left = Value((1173636544).toBackedInteger())

        val result = expressionInferer.infer(Equals(left, right))

        Assert.assertEquals(listOf(Reference(CALL_DATA, Value(0.toBackedInteger())) to Equals(right, left)), result)
    }
}
