package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.processors.FlowProcessor
import com.hileco.cortex.analysis.processors.ParameterProcessor
import com.hileco.cortex.constraints.Solution
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.InstructionsBuilder
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.ProgramRunner
import com.hileco.cortex.instructions.conditions.EQUALS
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.io.LOAD
import com.hileco.cortex.instructions.math.DIVIDE
import com.hileco.cortex.instructions.stack.PUSH
import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.ProgramStoreZone
import com.hileco.cortex.vm.VirtualMachine
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.math.BigInteger

class AttackProgramBuilderTest {
    private lateinit var solveableInstructions: List<Instruction>
    private lateinit var solution: Solution

    @Before
    fun obtainSolution() {
        val graphBuilder = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor()
        ))
        val instructionsBuilder = InstructionsBuilder()
        instructionsBuilder.includeIf({ conditionBuilder ->
            conditionBuilder.include { PUSH(2) }
            conditionBuilder.include { PUSH(123) }
            conditionBuilder.include { LOAD(ProgramStoreZone.CALL_DATA) }
            conditionBuilder.include { DIVIDE() }
            conditionBuilder.include { PUSH(12345678) }
            conditionBuilder.include { EQUALS() }
        }, { contentBuilder ->
            contentBuilder.include { HALT(WINNER) }
        })
        solveableInstructions = instructionsBuilder.build()
        val graph = graphBuilder.build(solveableInstructions)
        val attacker = Attacker(Attacker.TARGET_IS_HALT_WINNER)
        val solutions = attacker.solve(graph)
        solution = solutions[0]
    }

    @Test
    fun test() {
        val attackProgramBuilder = AttackProgramBuilder()
        val attackInstructions = attackProgramBuilder.build(123456789L, solution)
        val attackingProgram = Program(attackInstructions)
        val attackingProgramContext = ProgramContext(attackingProgram)
        val virtualMachine = VirtualMachine(attackingProgramContext)
        val solveableProgram = Program(solveableInstructions)
        virtualMachine.atlas[BigInteger.valueOf(123456789L)] = solveableProgram
        val programRunner = ProgramRunner(virtualMachine)
        try {
            programRunner.run()
            Assert.fail()
        } catch (e: ProgramException) {
            Assert.assertEquals(WINNER, e.reason)
        }
    }
}