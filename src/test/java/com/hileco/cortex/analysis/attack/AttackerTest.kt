package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.processors.FlowProcessor
import com.hileco.cortex.analysis.processors.JumpUnreachableProcessor
import com.hileco.cortex.analysis.processors.ParameterProcessor
import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.instructions.InstructionsBuilder
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.math.DIVIDE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.ProgramStoreZone.CALL_DATA
import org.junit.Assert
import org.junit.Test

class AttackerTest {
    @Test
    fun test() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor(),
                JumpUnreachableProcessor()
        ))
        val instructionsBuilder = InstructionsBuilder()
        instructionsBuilder.includeIf({ conditionBuilder ->
            conditionBuilder.include { PUSH(2) }
            conditionBuilder.include { PUSH(1) }
            conditionBuilder.include { LOAD(CALL_DATA) }
            conditionBuilder.include { DIVIDE() }
            conditionBuilder.include { PUSH(12345) }
            conditionBuilder.include { EQUALS() }
        }, { contentBuilder ->
            contentBuilder.include { HALT(WINNER) }
        })
        val instructions = instructionsBuilder.build()
        val graph = graphBuilder.build(instructions)
        val attacker = Attacker(Attacker.TARGET_IS_HALT_WINNER)
        val solutions = attacker.solve(graph)
        Documentation.of(Attacker::class.simpleName!!)
                .headingParagraph(Attacker::class.simpleName!!)
                .paragraph("Program:").source(instructions)
                .paragraph("Attack method:").source("TARGET_IS_HALT_WINNER")
                .paragraph("Suggested solution by Cortex:").source(solutions)
        Assert.assertEquals(1, solutions.size.toLong())
        val solution = solutions[0]
        Assert.assertTrue(solution.isSolvable)
        Assert.assertEquals(1, solution.possibleValues.size.toLong())
        val entry = solution.possibleValues.entries.first()
        Assert.assertEquals(Expression.Reference(CALL_DATA, Expression.Value(1L)), entry.key)
        Assert.assertEquals(java.lang.Long.valueOf(24690L), entry.value)
    }
}