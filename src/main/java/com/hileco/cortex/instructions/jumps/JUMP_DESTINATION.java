package com.hileco.cortex.instructions.jumps;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode
public class JUMP_DESTINATION implements Instruction {
    @Override
    public void execute(ProcessContext process, ProgramContext program) {
    }

    @Override
    public List<Integer> getStackAdds() {
        return Collections.emptyList();
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return Collections.emptyList();
    }

    @Override
    public List<StackParameter> getStackParameters() {
        return List.of();
    }

    @Override
    public String toString() {
        return "JUMP_DESTINATION";
    }
}
