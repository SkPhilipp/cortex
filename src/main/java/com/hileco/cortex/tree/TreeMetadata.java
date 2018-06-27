package com.hileco.cortex.tree;

import com.hileco.cortex.instructions.Instruction;

import java.util.List;
import java.util.stream.Stream;

public class TreeMetadata {

    @SuppressWarnings("unchecked")
    public static boolean isSelfContained(List<Instruction> instructions) {
        int added = 0;
        // TODO: Filter on a flag to operations which interact with things outside of the stack (ie. an "unknown" load)
        boolean readsWithinStack = true;
        for (Instruction instruction : instructions) {
            List<Integer> stackTakes = instruction.getOperation().getStackTakes(instruction.getOperands());
            List<Integer> stackAdds = instruction.getOperation().getStackAdds(instruction.getOperands());
            final int currentAdded = added;
            readsWithinStack &= Stream.concat(stackTakes.stream(), stackAdds.stream()).noneMatch(position -> position + 1 > currentAdded);
            added -= stackTakes.size();
            added += stackAdds.size();
        }
        return readsWithinStack;
    }
}
