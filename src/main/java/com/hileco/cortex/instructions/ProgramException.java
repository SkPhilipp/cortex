package com.hileco.cortex.instructions;

import com.hileco.cortex.vm.ProgramContext;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ProgramException extends Exception {

    private final ProgramContext programContext;
    private final Reason reason;

    @Override
    public String getMessage() {
        return String.format("%s: %s", this.reason.toString(), this.programContext.toString());
    }

    public enum Reason {
        JUMP_TO_ILLEGAL_INSTRUCTION,
        JUMP_OUT_OF_BOUNDS,
        INSTRUCTION_LIMIT_REACHED_ON_PROCESS_LEVEL,
        INSTRUCTION_LIMIT_REACHED_ON_PROGRAM_LEVEL,
        STACK_LIMIT_REACHED,
        RETURN_DATA_TOO_LARGE,
        STACK_TOO_FEW_ELEMENTS,
        CALL_RECIPIENT_MISSING,
        WINNER
    }
}
