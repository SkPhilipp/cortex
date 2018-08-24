package com.hileco.cortex.instructions.debug;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode
public class NOOP implements Instruction {
    public void execute(ProcessContext process, ProgramContext program) {
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

    public String toString() {
        return "NOOP";
    }
}
