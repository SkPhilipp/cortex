package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.instructions.ProgramException.Reason;

public class ProgramRunner {

    private final ProcessContext processContext;

    public ProgramRunner(ProcessContext processContext) {
        this.processContext = processContext;
    }

    @SuppressWarnings("unchecked")
    public void run() throws ProgramException {
        var programContext = this.processContext.getPrograms().peek();
        while (programContext.getInstructionPosition() != programContext.getProgram().getInstructions().size()) {
            var currentInstructionPosition = programContext.getInstructionPosition();
            var current = programContext.getProgram().getInstructions().get(currentInstructionPosition);
            current.execute(this.processContext, programContext);
            programContext = this.processContext.getPrograms().peek();
            if (programContext == null) {
                break;
            }
            if (programContext.getInstructionPosition() == currentInstructionPosition) {
                programContext.setInstructionPosition(currentInstructionPosition + 1);
            }
            programContext.setInstructionsExecuted(programContext.getInstructionsExecuted() + 1);
            if (programContext.getInstructionsExecuted() >= programContext.getInstructionLimit()) {
                throw new ProgramException(programContext, Reason.INSTRUCTION_LIMIT_REACHED_ON_PROGRAM_LEVEL);
            }
            this.processContext.setInstructionsExecuted(this.processContext.getInstructionsExecuted() + 1);
            if (this.processContext.getInstructionsExecuted() >= this.processContext.getInstructionLimit()) {
                throw new ProgramException(programContext, Reason.INSTRUCTION_LIMIT_REACHED_ON_PROCESS_LEVEL);
            }
        }
    }
}
