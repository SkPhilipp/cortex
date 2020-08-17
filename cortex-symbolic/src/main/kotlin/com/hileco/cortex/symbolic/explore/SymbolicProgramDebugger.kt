package com.hileco.cortex.symbolic.explore

import com.hileco.cortex.symbolic.explore.SymbolicInstructionRunner.StepMode
import com.hileco.cortex.symbolic.explore.SymbolicInstructionRunner.StepMode.NON_CONCRETE_JUMP_SKIP
import com.hileco.cortex.symbolic.explore.SymbolicInstructionRunner.StepMode.NON_CONCRETE_JUMP_TAKE
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import com.hileco.cortex.vm.ProgramException
import com.hileco.cortex.vm.ProgramException.Reason.*
import com.hileco.cortex.vm.ProgramRunner.Companion.INSTRUCTION_LIMIT

class SymbolicProgramDebugger(val virtualMachine: SymbolicVirtualMachine) {
    private val symbolicInstructionRunner = SymbolicInstructionRunner()

    fun stepSkip() {
        step(NON_CONCRETE_JUMP_SKIP)
    }

    fun stepTake() {
        step(NON_CONCRETE_JUMP_TAKE)
    }

    private fun step(stepMode: StepMode) {
        try {
            if (virtualMachine.exited) {
                return
            }
            if (virtualMachine.programs.isEmpty()) {
                virtualMachine.exited = true
                return
            }
            var programContext: SymbolicProgramContext = virtualMachine.programs.last()
            if (programContext.instructionPosition == programContext.program.instructionsLastPosition) {
                virtualMachine.exited = true
                return
            }
            val currentInstructionPosition = programContext.instructionPosition
            val positionedInstruction = programContext.program.instructionsAbsolute[currentInstructionPosition] ?: throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
            symbolicInstructionRunner.execute(positionedInstruction.instruction, virtualMachine, programContext, stepMode)
            if (virtualMachine.programs.isEmpty()) {
                virtualMachine.exited = true
                return
            }
            programContext = virtualMachine.programs.last()
            if (programContext.instructionPosition == currentInstructionPosition) {
                programContext.instructionPosition = currentInstructionPosition + positionedInstruction.instruction.width
            }
            programContext.instructionsExecuted++
            virtualMachine.instructionsExecuted++
            if (programContext.instructionsExecuted >= INSTRUCTION_LIMIT) {
                throw ProgramException(REACHED_LIMIT_INSTRUCTIONS_ON_PROGRAM)
            }
            if (virtualMachine.instructionsExecuted >= INSTRUCTION_LIMIT) {
                throw ProgramException(REACHED_LIMIT_INSTRUCTIONS_ON_VIRTUAL_MACHINE)
            }
        } catch (e: ProgramException) {
            virtualMachine.exited = true
            virtualMachine.exitedReason = e.reason
        } catch (e: Exception) {
            System.err.println("Process erred: ${e.message}")
            throw e
        }
    }
}
