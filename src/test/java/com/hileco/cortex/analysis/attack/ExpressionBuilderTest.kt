package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.analysis.edges.FlowType
import com.hileco.cortex.constraints.ExpressionGenerator
import com.hileco.cortex.constraints.expressions.Expression.*
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.math.DIVIDE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import com.hileco.cortex.vm.ProgramStoreZone.MEMORY
import org.junit.Assert
import org.junit.Test

class ExpressionBuilderTest {
    @Test
    fun test() {
        val expressionBuilder = ExpressionBuilder()
        val expression = expressionBuilder.build(BARRIER_01.instructions, listOf(
                Flow(FlowType.PROGRAM_FLOW, 0, 9),
                Flow(FlowType.PROGRAM_END, 9, null)
        ))
        val expressionGenerator = ExpressionGenerator()
        listOf(PUSH(2),
                PUSH(1),
                LOAD(CALL_DATA),
                DIVIDE(),
                PUSH(12345),
                EQUALS(),
                IS_ZERO()).forEach {
            expressionGenerator.addInstruction(it)
        }
        Assert.assertEquals(Not(expressionGenerator.currentExpression), expression)
    }

    @Test
    fun testStackConstraint() {
        val expressionBuilder = ExpressionBuilder()
        val constraintUsedCallDataGreaterThan5 = StackConstraint(
                { _, _, instruction -> instruction is LOAD && instruction.programStoreZone == CALL_DATA },
                { loadAddressPosition -> GreaterThan(Reference(CALL_DATA, loadAddressPosition), Value(5)) },
                LOAD.ADDRESS.position)
        val instructions = listOf(
                PUSH(1),
                LOAD(CALL_DATA),
                PUSH(2),
                LOAD(CALL_DATA),
                PUSH(3),
                LOAD(MEMORY))
        val expression = expressionBuilder.build(instructions, listOf(
                Flow(FlowType.PROGRAM_FLOW, 0, 3),
                Flow(FlowType.PROGRAM_END, 3, null)
        ), listOf(constraintUsedCallDataGreaterThan5))
        Assert.assertEquals("((CALL_DATA[1] > 5)) && ((CALL_DATA[2] > 5))", "$expression")
    }
}