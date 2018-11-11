package com.hileco.cortex.instructions.debug;

import com.hileco.cortex.context.VirtualMachine;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
public class NOOP implements Instruction {
    @Override
    public void execute(VirtualMachine process, ProgramContext program) {
    }

    @Override
    public List<Integer> getStackAdds() {
        return List.of();
    }

    @Override
    public List<ProgramZone> getInstructionModifiers() {
        return List.of();
    }

    @Override
    public List<StackParameter> getStackParameters() {
        return List.of();
    }

    @Override
    public String toString() {
        return "NOOP";
    }
}
