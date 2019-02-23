package com.hileco.cortex.vm.symbolic

import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason
import com.hileco.cortex.vm.ProgramConstants.Companion.INSTRUCTION_LIMIT

class SymbolicProgramRunner(private val virtualMachine: SymbolicVirtualMachine) {
    fun run() {
        if (virtualMachine.programs.isEmpty()) {
            return
        }
        var context: SymbolicProgramContext = virtualMachine.programs.peek()
        while (context.instructionPosition < context.program.instructions.size) {
            val currentInstructionPosition = context.instructionPosition
            val current = context.program.instructions[currentInstructionPosition]
            current.execute(virtualMachine, context)
            if (virtualMachine.programs.isEmpty()) {
                break
            }
            context = virtualMachine.programs.peek()
            if (context.instructionPosition == currentInstructionPosition) {
                context.instructionPosition = currentInstructionPosition + 1
            }
            context.instructionsExecuted++
            if (context.instructionsExecuted >= INSTRUCTION_LIMIT) {
                throw ProgramException(Reason.INSTRUCTION_LIMIT_REACHED_ON_PROGRAM)
            }
            virtualMachine.instructionsExecuted++
            if (virtualMachine.instructionsExecuted >= INSTRUCTION_LIMIT) {
                throw ProgramException(Reason.INSTRUCTION_LIMIT_REACHED_ON_VIRTUAL_MACHINE)
            }
        }
    }
}
