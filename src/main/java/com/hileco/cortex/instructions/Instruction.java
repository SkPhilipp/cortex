package com.hileco.cortex.instructions;


import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;

import java.util.List;
import java.util.stream.Collectors;

public interface Instruction {
    void execute(ProcessContext process, ProgramContext program) throws ProgramException;

    default List<Integer> getStackTakes() {
        return this.getStackParameters().stream().map(StackParameter::getPosition).collect(Collectors.toList());
    }

    List<Integer> getStackAdds();

    List<ProgramZone> getInstructionModifiers();

    List<StackParameter> getStackParameters();
}
