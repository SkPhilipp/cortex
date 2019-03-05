package com.hileco.cortex.instructions.jumps

import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.ProgramException
import com.hileco.cortex.instructions.ProgramException.Reason.JUMP_TO_ILLEGAL_INSTRUCTION
import com.hileco.cortex.instructions.ProgramException.Reason.JUMP_TO_OUT_OF_BOUNDS
import com.hileco.cortex.vm.concrete.ProgramContext
import com.hileco.cortex.vm.symbolic.SymbolicProgramContext

abstract class JumpingInstruction : Instruction() {
    fun performJump(programContext: ProgramContext, nextInstructionPosition: Int) {
        if (nextInstructionPosition < 0) {
            throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
        }
        val instructions = programContext.program.instructions
        if (nextInstructionPosition >= instructions.size) {
            throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
        }
        instructions[nextInstructionPosition] as? JUMP_DESTINATION ?: throw ProgramException(JUMP_TO_ILLEGAL_INSTRUCTION)
        programContext.instructionPosition = nextInstructionPosition
    }

    fun performJump(programContext: SymbolicProgramContext, nextInstructionPosition: Int) {
        if (nextInstructionPosition < 0) {
            throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
        }
        val instructions = programContext.program.instructions
        if (nextInstructionPosition >= instructions.size) {
            throw ProgramException(JUMP_TO_OUT_OF_BOUNDS)
        }
        instructions[nextInstructionPosition] as? JUMP_DESTINATION ?: throw ProgramException(JUMP_TO_ILLEGAL_INSTRUCTION)
        programContext.instructionPosition = nextInstructionPosition
    }
}
