package com.hileco.cortex.tree.stream;

import com.hileco.cortex.context.ProgramZone;
import com.hileco.cortex.tree.ProgramNode;
import com.hileco.cortex.tree.ProgramNodeType;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

import static com.hileco.cortex.context.ProgramZone.STACK;

public class Filters {

    public static Predicate<ProgramNode> instruction() {
        return node -> node != null && node.getType() == ProgramNodeType.INSTRUCTION;
    }

    public static Predicate<ProgramNode> operation(Class<?>... types) {
        return instruction().and(node -> {
            for (Class<?> type : types) {
                if (!type.isInstance(node.getInstruction().getOperation())) {
                    return false;
                }
            }
            return true;
        });
    }

    public static Predicate<ProgramNode> parameter(int index, Predicate<ProgramNode> predicate) {
        return node -> {
            if (node == null) {
                return false;
            }
            ProgramNode parameterNode = node.getParameters().get(index);
            if (parameterNode == null) {
                return false;
            }
            return predicate.test(parameterNode);
        };
    }

    public static Predicate<ProgramNode> parameters(Predicate<ProgramNode> predicate) {
        return node -> {
            if (node == null) {
                return false;
            }
            for (ProgramNode parameter : node.getParameters()) {
                if (!predicate.test(parameter)) {
                    return false;
                }
            }
            return true;
        };
    }

    private static final Set<ProgramZone> STACK_ZONE = Collections.singleton(STACK);

    public static Predicate<ProgramNode> isSelfContained() {
        return instruction().and(node -> {
            if (!STACK_ZONE.containsAll(node.getInstruction().getInstructionModifiers())) {
                return false;
            }
            return parameters(isSelfContained()).test(node);
        });
    }
}
