package com.hileco.cortex.instructions.jumps;

import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.vm.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
public class JUMP_DESTINATION implements Instruction {
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
        return "JUMP_DESTINATION";
    }
}
