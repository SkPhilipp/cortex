package com.hileco.cortex.instructions.debug;

import com.hileco.cortex.context.ProcessContext;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.StackParameter;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode
public class NOOP implements Instruction {
    @Override
    public void execute(ProcessContext process, ProgramContext program) {
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
        return Collections.emptyList();
    }

    @Override
    public List<StackParameter> getStackParameters() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "NOOP";
    }
}
