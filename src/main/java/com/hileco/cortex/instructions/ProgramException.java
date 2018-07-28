package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProgramContext;

public class ProgramException extends Exception {

    public enum Reason {
        JUMP_TO_ILLEGAL_INSTRUCTION,
        JUMP_OUT_OF_BOUNDS,
        INSTRUCTION_LIMIT_REACHED,
        STACK_LIMIT_REACHED,
        RETURN_DATA_TOO_LARGE
    }

    private ProgramContext programContext;
    private Reason reason;

    public ProgramException(ProgramContext core, Reason reason) {
        this.programContext = core;
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return String.format("%s: %s", reason.toString(), programContext.toString());
    }
}
