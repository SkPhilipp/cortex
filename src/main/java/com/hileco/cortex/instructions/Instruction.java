package com.hileco.cortex.instructions;


import com.hileco.cortex.vm.VirtualMachine;
import com.hileco.cortex.vm.ProgramContext;
import com.hileco.cortex.vm.ProgramZone;

import java.util.List;

public interface Instruction {
    void execute(VirtualMachine process, ProgramContext program) throws ProgramException;

    List<Integer> getStackAdds();

    List<ProgramZone> getInstructionModifiers();

    List<StackParameter> getStackParameters();
}
