package com.hileco.cortex.instructions.operations.jumps;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.operations.Operation;

import java.util.Collections;
import java.util.List;

public class EXIT implements Operation {
    public void execute(ProcessContext process, ProgramContext program) {
        process.getPrograms().clear();
    }

    public List<Integer> getStackTakes() {
        return Collections.emptyList();
    }

    public List<Integer> getStackAdds() {
        return Collections.emptyList();
    }

    public List<ProgramZone> getInstructionModifiers() {
        return Collections.singletonList(ProgramZone.PROGRAM_CONTEXT);
    }

    public String toString() {
        return "EXIT";
    }
}
