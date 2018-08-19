package com.hileco.cortex.instructions.jumps;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
public class EXIT implements Instruction {
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
