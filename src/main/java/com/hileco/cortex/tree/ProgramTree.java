package com.hileco.cortex.tree;

import lombok.Value;

import java.util.List;

@Value
public class ProgramTree {
    private final List<ProgramNode> nodes;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("        ┌───────────────────────────────────\n");
        for (ProgramNode node : nodes) {
            stringBuilder.append(node);
            stringBuilder.append('\n');
        }
        stringBuilder.append("        └───────────────────────────────────");
        return stringBuilder.toString();
    }
}
