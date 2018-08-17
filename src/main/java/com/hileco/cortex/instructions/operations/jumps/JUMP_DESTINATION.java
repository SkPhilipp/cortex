package com.hileco.cortex.instructions.operations.jumps;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.operations.Operation;

import java.util.Collections;
import java.util.List;

public class JUMP_DESTINATION implements Operation {
    public void execute(ProcessContext process, ProgramContext program) {
    }

    public List<Integer> getStackTakes() {
        return Collections.emptyList();
    }

    @Override
    public List<Integer> getStackAdds() {
        return Collections.emptyList();
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return Collections.emptyList();
    }

    public String toString() {
        return "JUMP_DESTINATION";
    }
}
