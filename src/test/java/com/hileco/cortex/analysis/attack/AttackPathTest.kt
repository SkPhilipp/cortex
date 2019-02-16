package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.analysis.edges.FlowType
import com.hileco.cortex.constraints.ExpressionGenerator
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.conditions.IS_ZERO
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.math.DIVIDE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import org.junit.Assert
import org.junit.Test

class AttackPathTest {
    @Test
    fun test() {
        val attackPath = AttackPath(BARRIER_01.instructions, listOf(
                Flow(FlowType.PROGRAM_FLOW, 0, 9),
                Flow(FlowType.PROGRAM_END, 9, null)
        ))
        val expression = attackPath.buildExpression()
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
        Assert.assertEquals(Expression.Not(expressionGenerator.currentExpression), expression)
    }
}