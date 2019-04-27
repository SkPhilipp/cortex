package com.hileco.cortex.instructions.stack

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionTest
import com.hileco.cortex.instructions.stack.ExecutionVariable.*
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class VARIABLETest : InstructionTest() {
    @Test
    fun document() {
        val instructions = listOf(VARIABLE(ADDRESS_SELF))
        val stack = this.run(instructions).stack
        Documentation.of("instructions/variable")
                .headingParagraph("VARIABLE")
                .paragraph("The VARIABLE adds the value of the referenced execution-bound variable to the stack.")
                .paragraph("Available execution-bound variables by name are: ${ExecutionVariable.values().joinToString { "$it" }}")
                .paragraph("Example program:").source(instructions)
                .paragraph("Resulting stack:").source(stack)
    }

    @Test
    fun testAddressSelf() {
        val instructions = listOf(VARIABLE(ADDRESS_SELF))
        val stack = this.run(instructions).stack
        Assert.assertEquals(stack.size(), 1)
        Assert.assertEquals(BigInteger(stack.pop()), 0.toBigInteger())
    }

    @Test
    fun testInstructionPosition() {
        val instructions = listOf(PUSH(1), POP(), VARIABLE(INSTRUCTION_POSITION))
        val stack = this.run(instructions).stack
        Assert.assertEquals(stack.size(), 1)
        Assert.assertEquals(BigInteger(stack.pop()), 2.toBigInteger())
    }

    @Test
    fun testAddressCaller() {
        val instructions = listOf(VARIABLE(ADDRESS_CALLER))
        val stack = this.run(instructions).stack
        Assert.assertEquals(stack.size(), 1)
        Assert.assertEquals(BigInteger(stack.pop()), 0.toBigInteger())
    }

    @Test
    fun testAddressOrigin() {
        val instructions = listOf(VARIABLE(ADDRESS_ORIGIN))
        val stack = this.run(instructions).stack
        Assert.assertEquals(stack.size(), 1)
        Assert.assertEquals(BigInteger(stack.pop()), 0.toBigInteger())
    }

    @Test
    fun testStartTime() {
        val startTime = System.currentTimeMillis()
        val instructions = listOf(VARIABLE(START_TIME))
        val stack = this.run(instructions) { virtualMachine, _ ->
            virtualMachine.variables[START_TIME] = startTime.toBigInteger()
        }.stack
        Assert.assertEquals(stack.size(), 1)
        Assert.assertEquals(BigInteger(stack.pop()), startTime.toBigInteger())
    }
}
