package com.hileco.cortex.instructions.operations;


import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.ProgramException;

import java.util.List;

public interface Operation {
    void execute(ProcessContext process, ProgramContext program) throws ProgramException;

    List<Integer> getStackTakes();

    List<Integer> getStackAdds();

    List<ProgramZone> getInstructionModifiers();
}
