package com.hileco.cortex.optimizerlow.strategies;

import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.Operations;
import com.hileco.cortex.instructions.Operations.Operation;
import com.hileco.cortex.instructions.ProgramBuilder;
import com.hileco.cortex.instructions.ProgramBuilderFactory;
import com.hileco.cortex.context.ProgramContext;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.ProgramRunner;
import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.optimizerlow.InstructionsOptimizeStrategy;
import com.hileco.cortex.context.layer.LayeredStack;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.hileco.cortex.context.ProgramZone.STACK;

@SuppressWarnings("unchecked")
public class PrecalculateSelfContainedStrategy implements InstructionsOptimizeStrategy {

    private final Set<ProgramZone> allowedZones;

    public PrecalculateSelfContainedStrategy() {
        this.allowedZones = new HashSet<>();
        this.allowedZones.add(STACK);
    }

    /**
     * Verifies whether an instruction will stay within only a stack of a given size, possibly adding to it.
     *
     * @return The boolean result along with the amount of stack elements added(>0) or removed(<0).
     */
    private Pair<Boolean, Integer> staysWithinStack(Instruction instruction, int stackSize) {
        Object operands = instruction.getOperands();
        Operation operation = instruction.getOperation();
        List<Integer> stackTakes = operation.getStackTakes(operands);
        List<Integer> stackAdds = operation.getStackAdds(operands);
        List<ProgramZone> instructionModifiers = operation.getInstructionModifiers(operands);
        return new Pair<>(
                Stream.concat(stackTakes.stream(), stackAdds.stream()).noneMatch(position -> position + 1 > stackSize)
                        && allowedZones.containsAll(instructionModifiers),
                stackAdds.size() - stackTakes.size()
        );
    }

    /**
     * Rewrites a list of optimizable instructions into a simplified equivalent.
     */
    private List<Instruction> rewrite(ProgramBuilderFactory programBuilderFactory, List<Instruction> optimizable) {
        ProgramContext programContext = new ProgramContext();
        ProgramRunner programRunner = new ProgramRunner(programContext);
        try {
            programRunner.run(optimizable);
        } catch (ProgramException e) {
            throw new IllegalStateException("Unknown cause for ProgramException", e);
        }
        LayeredStack<byte[]> stack = programContext.getStack();
        ProgramBuilder builder = programBuilderFactory.builder();
        for (byte[] bytes : stack) {
            builder = builder.PUSH(bytes);
        }
        // TODO: Preserve the original JUMP_DESTINATIONS before adding NOOP padding, this must account for stack size at all points
        for (int currentSize = builder.currentSize(); currentSize < optimizable.size(); currentSize++) {
            builder = builder.NOOP();
        }
        return builder.build();
    }

    @Override
    public void optimize(ProgramBuilderFactory programBuilderFactory, List<Instruction> instructions) {
        List<Instruction> result = new ArrayList<>();
        List<Instruction> optimizable = new ArrayList<>();
        int stackSize = 0;
        for (Instruction instruction : instructions) {
            Pair<Boolean, Integer> staysWithinStack = staysWithinStack(instruction, stackSize);
            if (staysWithinStack.getKey() && !(instruction.getOperation() instanceof Operations.JumpDestination)) {
                stackSize += staysWithinStack.getValue();
                optimizable.add(instruction);
            } else {
                result.addAll(rewrite(programBuilderFactory, optimizable));
                result.add(instruction);
                optimizable.clear();
                stackSize = 0;
            }
        }
        result.addAll(rewrite(programBuilderFactory, optimizable));
        instructions.clear();
        instructions.addAll(result);
    }
}
