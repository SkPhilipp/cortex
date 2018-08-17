package com.hileco.cortex.instructions.operations.jumps;

import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.operations.Operation;

import java.util.List;

import static com.hileco.cortex.instructions.ProgramException.Reason.JUMP_OUT_OF_BOUNDS;
import static com.hileco.cortex.instructions.ProgramException.Reason.JUMP_TO_ILLEGAL_INSTRUCTION;

abstract class JumpingOperation implements Operation {
    void performJump(ProgramContext program, int nextInstructionPosition) throws ProgramException {
        if (nextInstructionPosition < 0) {
            throw new ProgramException(program, JUMP_OUT_OF_BOUNDS);
        }
        List<Instruction> instructions = program.getProgram().getInstructions();
        if (nextInstructionPosition > instructions.size()) {
            throw new ProgramException(program, JUMP_OUT_OF_BOUNDS);
        }
        Instruction instruction = instructions.get(nextInstructionPosition);
        if (!(instruction.getOperation() instanceof JUMP_DESTINATION)) {
            throw new ProgramException(program, JUMP_TO_ILLEGAL_INSTRUCTION);
        }
        program.setInstructionPosition(nextInstructionPosition);
    }
}
