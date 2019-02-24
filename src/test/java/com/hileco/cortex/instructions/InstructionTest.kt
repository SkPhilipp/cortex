package com.hileco.cortex.instructions

import com.hileco.cortex.constraints.expressions.Expression
import com.hileco.cortex.vm.concrete.Program
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.concrete.ProgramRunner
import com.hileco.cortex.vm.concrete.VirtualMachine
import com.hileco.cortex.vm.symbolic.SymbolicProgram
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicVirtualMachine

open class InstructionTest {
    fun run(instruction: List<Instruction>): ProgramContext {
        return this.run(instruction) { _, _ -> }
    }

    fun run(instructions: List<Instruction>, customSetup: (VirtualMachine, ProgramContext) -> Unit): ProgramContext {
        val program = Program(instructions)
        val programContext = ProgramContext(program)
        val virtualMachine = VirtualMachine(programContext)
        customSetup(virtualMachine, programContext)
        val programRunner = ProgramRunner(virtualMachine)
        programRunner.run()
        return programContext
    }

    fun runSymbolic(instruction: Instruction, vararg stack: Expression): Expression {
        val symbolicProgramContext = SymbolicProgramContext(SymbolicProgram(listOf()))
        val symbolicVirtualMachine = SymbolicVirtualMachine(symbolicProgramContext)
        stack.reversed().forEach {
            symbolicProgramContext.stack.push(it)
        }
        instruction.execute(symbolicVirtualMachine, symbolicProgramContext)
        return symbolicProgramContext.stack.peek()
    }
}