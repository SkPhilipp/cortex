package com.hileco.cortex.tree;

import java.util.function.Function;

public enum ProgramNodeType {
    INSTRUCTION(programNode -> {
        return String.format("(%06d INSTRUCTION %s)",
                programNode.getLine(),
                programNode.getInstruction().toString().trim()
        );
    }),
    JUMP_DESTINATION(programNode -> {
        return String.format("(%06d JUMP_DESTINATION)",
                programNode.getLine());
    });

    private Function<ProgramNode, String> formatter;

    ProgramNodeType(Function<ProgramNode, String> formatter) {
        this.formatter = formatter;
    }

    public String format(ProgramNode programNode) {
        return formatter.apply(programNode);
    }
}
