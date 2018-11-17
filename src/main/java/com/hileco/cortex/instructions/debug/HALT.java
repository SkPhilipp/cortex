package com.hileco.cortex.instructions.debug;

import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.vm.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.StackParameter;
import lombok.Value;

import java.util.List;

@Value
public class HALT implements Instruction {
    private ProgramException.Reason reason;

    @Override
    public void execute(VirtualMachine process, ProgramContext program) throws ProgramException {
        throw new ProgramException(program, this.reason);
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
        return String.format("HALT %s", this.reason);
    }
}
