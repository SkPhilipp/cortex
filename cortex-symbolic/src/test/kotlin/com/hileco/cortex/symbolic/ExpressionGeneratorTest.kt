package com.hileco.cortex.symbolic

import com.hileco.cortex.symbolic.expressions.Expression.*
import com.hileco.cortex.symbolic.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.collections.BackedInteger.Companion.ONE_32
import com.hileco.cortex.collections.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.collections.toBackedInteger
import com.hileco.cortex.symbolic.instructions.debug.DROP
import com.hileco.cortex.symbolic.instructions.io.LOAD
import com.hileco.cortex.symbolic.instructions.math.ADD
import com.hileco.cortex.symbolic.instructions.math.SUBTRACT
import com.hileco.cortex.symbolic.instructions.stack.DUPLICATE
import com.hileco.cortex.symbolic.instructions.stack.POP
import com.hileco.cortex.symbolic.instructions.stack.PUSH
import com.hileco.cortex.symbolic.instructions.stack.SWAP
import org.junit.Assert
import org.junit.Test

class ExpressionGeneratorTest {
    @Test
    fun testParameterized() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(123.toBackedInteger()))
        builder.addInstruction(PUSH(123.toBackedInteger()))
        builder.addInstruction(SUBTRACT())
        Assert.assertEquals(Value(ZERO_32), builder.currentExpression)
    }

    @Test
    fun testPop() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(123.toBackedInteger()))
        builder.addInstruction(PUSH(ONE_32))
        builder.addInstruction(POP())
        builder.addInstruction(PUSH(321.toBackedInteger()))
        builder.addInstruction(PUSH(ONE_32))
        builder.addInstruction(POP())
        builder.addInstruction(SUBTRACT())
        val result = builder.currentExpression
        Assert.assertTrue(result is Value)
        Assert.assertEquals(198.toBackedInteger(), (result as Value).constant)
    }

    @Test
    fun testDrop() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(3.toBackedInteger()))
        builder.addInstruction(PUSH(2.toBackedInteger()))
        builder.addInstruction(PUSH(ONE_32))
        builder.addInstruction(DROP(2))
        val result = builder.currentExpression
        Assert.assertTrue(result is Value)
        Assert.assertEquals(3.toBackedInteger(), (result as Value).constant)
    }

    @Test
    fun testReferences() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(10.toBackedInteger()))
        builder.addInstruction(LOAD(CALL_DATA))
        Assert.assertEquals(VariableExtract(CALL_DATA, Value(10.toBackedInteger())), builder.currentExpression)
    }

    @Test
    fun testMissing() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(123.toBackedInteger()))
        builder.addInstruction(SUBTRACT())
        Assert.assertEquals(Subtract(Value(123.toBackedInteger()), Stack(0)), builder.currentExpression)
    }

    @Test
    fun testMultipleExpressions() {
        val instructions = listOf(
                PUSH(456.toBackedInteger()),
                PUSH(456.toBackedInteger()),
                ADD(),
                PUSH(123.toBackedInteger()),
                PUSH(123.toBackedInteger()),
                SUBTRACT())
        val builder = ExpressionGenerator()
        instructions.forEach { builder.addInstruction(it) }

        val result = builder.currentExpression
        val resultOffset1 = builder.viewExpression(1)
        Assert.assertTrue(result is Value)
        Assert.assertEquals(ZERO_32, (result as Value).constant)
        Assert.assertTrue(resultOffset1 is Value)
        Assert.assertEquals(912.toBackedInteger(), (resultOffset1 as Value).constant)
    }

    @Test
    fun testDuplicate() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(456.toBackedInteger()))
        builder.addInstruction(PUSH(456.toBackedInteger()))
        builder.addInstruction(ADD())
        builder.addInstruction(DUPLICATE(0))
        Assert.assertEquals(builder.viewExpression(1), builder.currentExpression)
    }

    @Test
    fun testSwap() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(456.toBackedInteger()))
        builder.addInstruction(PUSH(456.toBackedInteger()))
        builder.addInstruction(ADD())
        builder.addInstruction(PUSH(123.toBackedInteger()))
        builder.addInstruction(PUSH(123.toBackedInteger()))
        builder.addInstruction(SUBTRACT())
        val topExpression = builder.currentExpression
        val nextExpression = builder.viewExpression(1)
        builder.addInstruction(SWAP(0, 1))
        Assert.assertEquals(nextExpression, builder.currentExpression)
        Assert.assertEquals(topExpression, builder.viewExpression(1))
    }
}
