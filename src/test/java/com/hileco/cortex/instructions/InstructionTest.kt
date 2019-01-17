package com.hileco.cortex.instructions

import com.hileco.cortex.vm.Program
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine

open class InstructionTest {
    fun run(instruction: List<Instruction>): ProgramContext {
        return this.run(instruction) { _, _ -> }
    }

    fun run(instructions: List<Instruction>, customSetup: (VirtualMachine, ProgramContext) -> Unit): ProgramContext {
        val program = Program(instructions)
        val programContext = ProgramContext(program)
        val processContext = VirtualMachine(programContext)
        customSetup(processContext, programContext)
        val programRunner = ProgramRunner(processContext)
        programRunner.run()
        return programContext
    }
}