package com.hileco.cortex.vm.concrete

import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason
import com.hileco.cortex.vm.ProgramConstants.Companion.INSTRUCTION_LIMIT

class ProgramRunner(private val virtualMachine: VirtualMachine) {
    fun run() {
        if (virtualMachine.programs.isEmpty()) {
            return
        }
        var programContext: ProgramContext = virtualMachine.programs.peek()
        while (programContext.instructionPosition < programContext.program.instructions.size) {
            val currentInstructionPosition = programContext.instructionPosition
            val instruction = programContext.program.instructions[currentInstructionPosition]
            instruction.execute(virtualMachine, programContext)
            if (virtualMachine.programs.isEmpty()) {
                break
            }
            programContext = virtualMachine.programs.peek()
            if (programContext.instructionPosition == currentInstructionPosition) {
                programContext.instructionPosition = currentInstructionPosition + 1
            }
            programContext.instructionsExecuted++
            virtualMachine.instructionsExecuted++
            if (programContext.instructionsExecuted >= INSTRUCTION_LIMIT) {
                throw ProgramException(Reason.INSTRUCTION_LIMIT_REACHED_ON_PROGRAM)
            }
            if (virtualMachine.instructionsExecuted >= INSTRUCTION_LIMIT) {
                throw ProgramException(Reason.INSTRUCTION_LIMIT_REACHED_ON_VIRTUAL_MACHINE)
            }
        }
    }
}
