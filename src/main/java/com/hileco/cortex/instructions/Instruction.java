package com.hileco.cortex.instructions;


import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;

import java.util.List;

public interface Instruction {
    void execute(ProcessContext process, ProgramContext program) throws ProgramException;

    List<Integer> getStackAdds();

    List<ProgramZone> getInstructionModifiers();

    List<StackParameter> getStackParameters();
}
