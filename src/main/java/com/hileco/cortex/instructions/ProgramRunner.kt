package com.hileco.cortex.instructions

import com.hileco.cortex.instructions.ProgramException.Reason
import com.hileco.cortex.vm.ProgramContext
import com.hileco.cortex.vm.VirtualMachine

class ProgramRunner(private val virtualMachine: VirtualMachine) {

    @Throws(ProgramException::class)
    fun run() {
        var context: ProgramContext? = virtualMachine.programs.peek()
        while (context != null && context.instructionPosition != context.program.instructions.size) {
            val currentInstructionPosition = context.instructionPosition
            val current = context.program.instructions[currentInstructionPosition]
            current.execute(virtualMachine, context)
            context = virtualMachine.programs.peek()
            if (context == null) {
                break
            }
            if (context.instructionPosition == currentInstructionPosition) {
                context.instructionPosition = currentInstructionPosition + 1
            }
            context.instructionsExecuted++
            if (context.instructionsExecuted >= context.instructionLimit) {
                throw ProgramException(context, Reason.INSTRUCTION_LIMIT_REACHED_ON_PROGRAM_LEVEL)
            }
            virtualMachine.instructionsExecuted++
            if (virtualMachine.instructionsExecuted >= virtualMachine.instructionLimit) {
                throw ProgramException(context, Reason.INSTRUCTION_LIMIT_REACHED_ON_PROCESS_LEVEL)
            }
        }
    }
}
