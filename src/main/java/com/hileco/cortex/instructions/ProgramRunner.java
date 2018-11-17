package com.hileco.cortex.instructions;

import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.instructions.ProgramException.Reason;

public class ProgramRunner {

    private final VirtualMachine virtualMachine;

    public ProgramRunner(VirtualMachine virtualMachine) {
        this.virtualMachine = virtualMachine;
    }

    @SuppressWarnings("unchecked")
    public void run() throws ProgramException {
        var programContext = this.virtualMachine.getPrograms().peek();
        while (programContext.getInstructionPosition() != programContext.getProgram().getInstructions().size()) {
            var currentInstructionPosition = programContext.getInstructionPosition();
            var current = programContext.getProgram().getInstructions().get(currentInstructionPosition);
            current.execute(this.virtualMachine, programContext);
            programContext = this.virtualMachine.getPrograms().peek();
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
            this.virtualMachine.setInstructionsExecuted(this.virtualMachine.getInstructionsExecuted() + 1);
            if (this.virtualMachine.getInstructionsExecuted() >= this.virtualMachine.getInstructionLimit()) {
                throw new ProgramException(programContext, Reason.INSTRUCTION_LIMIT_REACHED_ON_PROCESS_LEVEL);
            }
        }
    }
}
