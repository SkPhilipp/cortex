package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.analysis.edges.FlowType
import com.hileco.cortex.symbolic.ExpressionGenerator
import com.hileco.cortex.symbolic.expressions.Expression.*
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.vm.bytes.BackedInteger.Companion.ONE_32
import com.hileco.cortex.vm.bytes.toBackedInteger
import com.hileco.cortex.vm.instructions.conditions.EQUALS
import com.hileco.cortex.vm.instructions.conditions.IS_ZERO
import com.hileco.cortex.vm.instructions.io.LOAD
import com.hileco.cortex.vm.instructions.math.DIVIDE
import com.hileco.cortex.vm.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Test

class ExpressionBuilderTest {
    @Test
    fun test() {
        val expressionBuilder = ExpressionBuilder()
        val expression = expressionBuilder.build(BARRIER_01.instructions, listOf(
                Flow(FlowType.PROGRAM_FLOW, 0, 9),
                Flow(FlowType.PROGRAM_END, 9, null)
        ), listOf())
        val expressionGenerator = ExpressionGenerator()
        listOf(
                PUSH(2.toBackedInteger()),
                PUSH(1.toBackedInteger()),
                LOAD(CALL_DATA),
                DIVIDE(),
                PUSH(12345.toBackedInteger()),
                EQUALS(),
                IS_ZERO()
        ).forEach {
            expressionGenerator.addInstruction(it)
        }
        Assert.assertEquals(Not(expressionGenerator.currentExpression), expression)
    }

    @Test
    fun testStackConstraint() {
        val expressionBuilder = ExpressionBuilder()
        val constraintUsedCallDataGreaterThan5 = StackConstraint(
                { _, _, instruction -> instruction is LOAD && instruction.programStoreZone == CALL_DATA },
                { loadAddressPosition -> GreaterThan(Reference(CALL_DATA, loadAddressPosition), Value(5.toBackedInteger())) },
                LOAD.ADDRESS.position)
        val instructions = listOf(
                PUSH(1.toBackedInteger()),
                LOAD(CALL_DATA),
                PUSH(2.toBackedInteger()),
                LOAD(CALL_DATA),
                PUSH(3.toBackedInteger()),
                LOAD(MEMORY))
        val expression = expressionBuilder.build(instructions, listOf(
                Flow(FlowType.PROGRAM_FLOW, 0, 3),
                Flow(FlowType.PROGRAM_END, 3, null)
        ), listOf(constraintUsedCallDataGreaterThan5))
        val expectedExpression = And(listOf(
                GreaterThan(Reference(CALL_DATA, Value(ONE_32)), Value(5.toBackedInteger())),
                GreaterThan(Reference(CALL_DATA, Value(2.toBackedInteger())), Value(5.toBackedInteger())))
        )
        Assert.assertEquals(expectedExpression, expression)
    }
}