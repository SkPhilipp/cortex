package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.symbolic.Solution
import com.hileco.cortex.vm.*
import com.hileco.cortex.vm.barrier.BarrierProgram.Companion.BARRIER_01
import com.hileco.cortex.vm.bytes.toBackedInteger
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AttackProgramBuilderTest {
    private lateinit var solution: Solution

    @Before
    fun obtainSolution() {
        val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(BARRIER_01.instructions)
        val attacker = Attacker(Attacker.TARGET_IS_HALT_WINNER)
        val solutions = attacker.solve(graph)
        solution = solutions.first()
    }

    @Test
    fun test() {
        val attackProgramBuilder = AttackProgramBuilder()
        val attackInstructions = attackProgramBuilder.build(SOLVEABLE_ADDRESS, solution)
        val attackingProgram = Program(attackInstructions)
        val attackingProgramContext = ProgramContext(attackingProgram)
        val virtualMachine = VirtualMachine(attackingProgramContext)
        val solveableProgram = Program(BARRIER_01.instructions)
        virtualMachine.atlas[SOLVEABLE_ADDRESS] = solveableProgram
        val programRunner = ProgramRunner(virtualMachine)
        try {
            programRunner.run()
            Assert.fail()
        } catch (e: ProgramException) {
            Assert.assertEquals(ProgramException.Reason.WINNER, e.reason)
        }
        Documentation.of(AttackProgramBuilder::class.java.simpleName)
                .headingParagraph(AttackProgramBuilder::class.java.simpleName)
                .paragraph("Program at address $SOLVEABLE_ADDRESS:").source(BARRIER_01.instructions)
                .paragraph("Attack method:").source("TARGET_IS_HALT_WINNER")
                .paragraph("Suggested program-solution by Cortex:").source(attackInstructions)
    }

    companion object {
        val SOLVEABLE_ADDRESS = 0x123456789.toBackedInteger()
    }
}