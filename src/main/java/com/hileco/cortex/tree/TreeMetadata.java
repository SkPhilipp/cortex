package com.hileco.cortex.tree;

import com.hileco.cortex.instructions.Instruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.hileco.cortex.instructions.Operations.Add;
import static com.hileco.cortex.instructions.Operations.BitwiseAnd;
import static com.hileco.cortex.instructions.Operations.BitwiseNot;
import static com.hileco.cortex.instructions.Operations.BitwiseOr;
import static com.hileco.cortex.instructions.Operations.BitwiseXor;
import static com.hileco.cortex.instructions.Operations.Divide;
import static com.hileco.cortex.instructions.Operations.Duplicate;
import static com.hileco.cortex.instructions.Operations.Equals;
import static com.hileco.cortex.instructions.Operations.Exit;
import static com.hileco.cortex.instructions.Operations.GreaterThan;
import static com.hileco.cortex.instructions.Operations.Hash;
import static com.hileco.cortex.instructions.Operations.IsZero;
import static com.hileco.cortex.instructions.Operations.Jump;
import static com.hileco.cortex.instructions.Operations.JumpDestination;
import static com.hileco.cortex.instructions.Operations.JumpIf;
import static com.hileco.cortex.instructions.Operations.LessThan;
import static com.hileco.cortex.instructions.Operations.Load;
import static com.hileco.cortex.instructions.Operations.Modulo;
import static com.hileco.cortex.instructions.Operations.Multiply;
import static com.hileco.cortex.instructions.Operations.NoOp;
import static com.hileco.cortex.instructions.Operations.Operands;
import static com.hileco.cortex.instructions.Operations.Operation;
import static com.hileco.cortex.instructions.Operations.Pop;
import static com.hileco.cortex.instructions.Operations.Push;
import static com.hileco.cortex.instructions.Operations.Save;
import static com.hileco.cortex.instructions.Operations.Subtract;
import static com.hileco.cortex.instructions.Operations.Swap;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class TreeMetadata {

    public static class OperationMetadata<T extends Operation<V>, V extends Operands> {
        private Function<V, List<Integer>> stackReadPositionsMapper;
        private Function<V, List<Integer>> stackWritePositionsMapper;

        public OperationMetadata(Function<V, List<Integer>> stackReadPositionsMapper, Function<V, List<Integer>> stackWritePositionsMapper) {
            this.stackReadPositionsMapper = stackReadPositionsMapper;
            this.stackWritePositionsMapper = stackWritePositionsMapper;
        }

        public List<Integer> getStackTakes(Instruction<T, V> instruction) {
            return stackReadPositionsMapper.apply(instruction.getOperands());
        }

        public List<Integer> getStackAdds(Instruction<T, V> instruction) {
            return stackWritePositionsMapper.apply(instruction.getOperands());
        }
    }

    private static final Map<Class<? extends Operation>, OperationMetadata<?, ?>> METADATA = new HashMap<>();

    static {
        register(Push.class, operands -> emptyList(), operands -> singletonList(-1));
        register(Pop.class, operands -> singletonList(0), operands -> emptyList());
        register(Swap.class, operands -> asList(operands.topOffsetLeft, operands.topOffsetRight), operands -> asList(operands.topOffsetRight, operands.topOffsetLeft));
        register(Duplicate.class, operands -> singletonList(operands.topOffset), operands -> singletonList(-1));
        register(BitwiseAnd.class, operands -> asList(0, 1), operands -> singletonList(-1));
        register(BitwiseNot.class, operands -> asList(0, 1), operands -> singletonList(-1));
        register(BitwiseOr.class, operands -> asList(0, 1), operands -> singletonList(-1));
        register(BitwiseXor.class, operands -> asList(0, 1), operands -> singletonList(-1));
        register(Hash.class, operands -> singletonList(0), operands -> singletonList(-1));
        register(Equals.class, operands -> asList(0, 1), operands -> singletonList(-1));
        register(GreaterThan.class, operands -> asList(0, 1), operands -> singletonList(-1));
        register(LessThan.class, operands -> asList(0, 1), operands -> singletonList(-1));
        register(IsZero.class, operands -> singletonList(0), operands -> singletonList(-1));
        register(Add.class, operands -> asList(0, 1), operands -> singletonList(-1));
        register(Subtract.class, operands -> asList(0, 1), operands -> singletonList(-1));
        register(Multiply.class, operands -> asList(0, 1), operands -> singletonList(-1));
        register(Divide.class, operands -> asList(0, 1), operands -> singletonList(-1));
        register(Modulo.class, operands -> singletonList(0), operands -> singletonList(-1));
        register(Jump.class, operands -> singletonList(0), operands -> emptyList());
        register(JumpDestination.class, operands -> emptyList(), operands -> emptyList());
        register(NoOp.class, operands -> emptyList(), operands -> emptyList());
        register(JumpIf.class, operands -> asList(0, 1), operands -> emptyList());
        register(Exit.class, operands -> emptyList(), operands -> emptyList());
        register(Save.class, operands -> asList(0, 1), operands -> emptyList());
        register(Load.class, operands -> singletonList(0), operands -> singletonList(-1));
    }

    private static <T extends Operation<V>, V extends Operands> void register(Class<T> instructionClass,
                                                                              Function<V, List<Integer>> stackReadPositionsMapper,
                                                                              Function<V, List<Integer>> stackWritePositionsMapper) {
        METADATA.put(instructionClass, new OperationMetadata<T, V>(stackReadPositionsMapper, stackWritePositionsMapper));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Operation<V>, V extends Operands> OperationMetadata<T, V> get(Class<Operation<V>> operationClass) {
        return (OperationMetadata<T, V>) METADATA.get(operationClass);
    }

    @SuppressWarnings("unchecked")
    public static boolean isSelfContained(List<Instruction> instructions) {
        int added = 0;
        // TODO: Filter on a flag to operations which interact with things outside of the stack (ie. an "unknown" load)
        boolean readsWithinStack = true;
        for (Instruction instruction : instructions) {
            OperationMetadata<?, ?> metadata = METADATA.get(instruction.getOperation().getClass());
            List<Integer> stackTakes = metadata.getStackTakes(instruction);
            List<Integer> stackAdds = metadata.getStackAdds(instruction);
            final int currentAdded = added;
            readsWithinStack &= Stream.concat(stackTakes.stream(), stackAdds.stream()).noneMatch(position -> position + 1 > currentAdded);
            added -= stackTakes.size();
            added += stackAdds.size();
        }
        return readsWithinStack;
    }
}
