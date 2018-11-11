package com.hileco.cortex.instructions;


import com.hileco.cortex.context.VirtualMachine;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;

import java.util.List;

public interface Instruction {
    void execute(VirtualMachine process, ProgramContext program) throws ProgramException;

    List<Integer> getStackAdds();

    List<ProgramZone> getInstructionModifiers();

    List<StackParameter> getStackParameters();
}
