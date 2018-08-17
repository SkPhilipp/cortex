package com.hileco.cortex.instructions.debug;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.Instruction;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
public class HALT implements Instruction {
    private ProgramException.Reason reason;

    public void execute(ProcessContext process, ProgramContext program) throws ProgramException {
        throw new ProgramException(program, reason);
    }

    public List<Integer> getStackTakes() {
        return Collections.emptyList();
    }

    public List<Integer> getStackAdds() {
        return Collections.emptyList();
    }

    public List<ProgramZone> getInstructionModifiers() {
        return Collections.emptyList();
    }
}
