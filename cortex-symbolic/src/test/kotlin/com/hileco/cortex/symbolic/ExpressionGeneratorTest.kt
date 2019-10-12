package com.hileco.cortex.symbolic

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.instructions.debug.DROP
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.math.ADD
import com.hileco.cortex.vm.instructions.math.SUBTRACT
import com.hileco.cortex.vm.instructions.stack.DUPLICATE
import com.hileco.cortex.vm.instructions.stack.POP
import com.hileco.cortex.vm.instructions.stack.PUSH
import com.hileco.cortex.vm.instructions.stack.SWAP
import org.junit.Assert
import org.junit.Test

class ExpressionGeneratorTest {
    @Test
    fun testParameterized() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(123))
        builder.addInstruction(PUSH(123))
        builder.addInstruction(SUBTRACT())
        Assert.assertEquals("0", builder.currentExpression.toString())
    }

    @Test
    fun testPop() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(321))
        builder.addInstruction(PUSH(1))
        builder.addInstruction(POP())
        builder.addInstruction(PUSH(123))
        builder.addInstruction(PUSH(1))
        builder.addInstruction(POP())
        builder.addInstruction(SUBTRACT())
        Assert.assertEquals("-198", builder.currentExpression.toString())
    }

    @Test
    fun testDrop() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(3))
        builder.addInstruction(PUSH(2))
        builder.addInstruction(PUSH(1))
        builder.addInstruction(DROP(2))
        Assert.assertEquals("3", builder.currentExpression.toString())
    }

    @Test
    fun testReferences() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(10))
        builder.addInstruction(LOAD(ProgramStoreZone.CALL_DATA))
        Assert.assertEquals("CALL_DATA[10]", builder.currentExpression.toString())
    }

    @Test
    fun testMissing() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(123))
        builder.addInstruction(SUBTRACT())
        Assert.assertEquals("(123 - STACK[0])", builder.currentExpression.toString())
    }

    @Test
    fun testMultipleExpressions() {
        val instructions = listOf(
                PUSH(456),
                PUSH(456),
                ADD(),
                PUSH(123),
                PUSH(123),
                SUBTRACT())
        val builder = ExpressionGenerator()
        instructions.forEach { builder.addInstruction(it) }
        Documentation.of(ExpressionGenerator::class.simpleName!!)
                .headingParagraph(ExpressionGenerator::class.simpleName!!)
                .paragraph("Program:").source(instructions)
                .paragraph("Resulting expressions:").source(builder.viewAllExpressions())
        Assert.assertEquals("0", builder.currentExpression.toString())
        Assert.assertEquals("912", builder.viewExpression(1).toString())
    }

    @Test
    fun testDuplicate() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(456))
        builder.addInstruction(PUSH(456))
        builder.addInstruction(ADD())
        builder.addInstruction(DUPLICATE(0))
        Assert.assertEquals(builder.viewExpression(1).toString(), builder.currentExpression.toString())
    }

    @Test
    fun testSwap() {
        val builder = ExpressionGenerator()
        builder.addInstruction(PUSH(456))
        builder.addInstruction(PUSH(456))
        builder.addInstruction(ADD())
        builder.addInstruction(PUSH(123))
        builder.addInstruction(PUSH(123))
        builder.addInstruction(SUBTRACT())
        val topExpression = builder.currentExpression
        val nextExpression = builder.viewExpression(1)
        builder.addInstruction(SWAP(0, 1))
        Assert.assertEquals(nextExpression, builder.currentExpression)
        Assert.assertEquals(topExpression, builder.viewExpression(1))
    }
}
