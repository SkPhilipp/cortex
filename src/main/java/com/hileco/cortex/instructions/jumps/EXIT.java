package com.hileco.cortex.instructions.jumps;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode
public class EXIT implements Instruction {
    @Override
    public void execute(ProcessContext process, ProgramContext program) {
        process.getPrograms().clear();
    }

    @Override
    public List<Integer> getStackTakes() {
        return Collections.emptyList();
    }

    @Override
    public List<Integer> getStackAdds() {
        return Collections.emptyList();
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return Collections.singletonList(ProgramZone.PROGRAM_CONTEXT);
    }

    @Override
    public String toString() {
        return "EXIT";
    }
}
