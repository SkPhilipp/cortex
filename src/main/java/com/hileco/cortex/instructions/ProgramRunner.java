package com.hileco.cortex.instructions;

import com.hileco.cortex.instructions.ProgramException.Reason;

import java.util.List;

import static com.hileco.cortex.instructions.ProgramException.Reason.JUMP_OUT_OF_BOUNDS;
import static com.hileco.cortex.instructions.ProgramException.Reason.JUMP_TO_ILLEGAL_INSTRUCTION;
import static com.hileco.cortex.instructions.ProgramException.Reason.STACK_LIMIT_REACHED;

public class ProgramRunner {
    private ProgramContext programContext;

    public ProgramRunner(ProgramContext programContext) {
        this.programContext = programContext;
    }

    @SuppressWarnings("unchecked")
    public void run(List<Instruction> instructions) throws ProgramException {
        int size = instructions.size();
        System.out.println();
        System.out.println(String.format("[   SYSTEM   ] Executing program of size %d", size));
        while (!programContext.isExiting() && programContext.getInstructionPosition() != size) {
            checkInstructionPosition(size);
            Instruction current = instructions.get(programContext.getInstructionPosition());
            checkJumping(current);
            System.out.println(String.format("[ %10d ] %-15s", programContext.getInstructionPosition(), current.executor.toString()));
            incrementInstructionPosition(current);
            current.executor.execute(programContext, current.data);
            incrementInstructionsExecuted();
            checkStack();
            manageInstructionExecution();
        }
    }

    private void incrementInstructionsExecuted() {
        programContext.setInstructionsExecuted(programContext.getInstructionsExecuted() + 1);
    }

    private void incrementInstructionPosition(Instruction current) {
        if (!(current.executor instanceof Instructions.Jump)) {
            programContext.setInstructionPosition(programContext.getInstructionPosition() + 1);
        }
    }

    private void checkInstructionPosition(int size) throws ProgramException {
        if (programContext.getInstructionPosition() < 0) {
            throw new ProgramException(programContext, JUMP_OUT_OF_BOUNDS);
        }
        if (programContext.getInstructionPosition() > size) {
            throw new ProgramException(programContext, JUMP_OUT_OF_BOUNDS);
        }
    }

    private void checkJumping(Instruction current) throws ProgramException {
        if (programContext.isJumping()
                && !(current.executor instanceof Instructions.JumpDestination)) {
            throw new ProgramException(programContext, JUMP_TO_ILLEGAL_INSTRUCTION);
        }
    }

    private void checkStack() throws ProgramException {
        if (programContext.getStack().size() >= programContext.getStackLimit()) {
            throw new ProgramException(programContext, STACK_LIMIT_REACHED);
        }
    }

    private void manageInstructionExecution() throws ProgramException {
        if (programContext.getInstructionsExecuted() >= programContext.getInstructionLimit()) {
            throw new ProgramException(programContext, Reason.INSTRUCTION_LIMIT_REACHED);
        }
    }
}
