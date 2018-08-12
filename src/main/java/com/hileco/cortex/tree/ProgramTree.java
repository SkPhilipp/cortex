package com.hileco.cortex.tree;

import lombok.Value;

import java.util.List;

@Value
public class ProgramTree {
    private final List<ProgramNode> nodes;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ProgramNode node : nodes) {
            stringBuilder.append(node);
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }
}
