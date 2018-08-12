package com.hileco.cortex.instructions;

import com.hileco.cortex.context.ProgramZone;
import lombok.Value;

import java.util.List;

import static com.hileco.cortex.instructions.Operations.Operation;

@Value
public class Instruction<T extends Operation<V>, V> {
    private final T operation;
    private final V operands;

    // TODO: Use these everywhere
    public List<Integer> getStackTakes() {
        return getOperation().getStackTakes(operands);
    }

    // TODO: Use these everywhere
    public List<Integer> getStackAdds() {
        return getOperation().getStackAdds(operands);
    }

    // TODO: Use these everywhere
    public List<ProgramZone> getInstructionModifiers() {
        return getOperation().getInstructionModifiers(operands);
    }

    @Override
    public String toString() {
        return String.format("%s %s", operation, operands);
    }
}
