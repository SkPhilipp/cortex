package com.hileco.cortex.tree;

public enum ProgramNodeType {

    INSTRUCTION((programNode, stringBuilder, offset) -> {
        stringBuilder.append(String.format("%06d │", programNode.getLine()));
        for (int i = 0; i < offset; i++) {
            stringBuilder.append(' ');
        }
        stringBuilder.append(' ');
        stringBuilder.append(programNode.getInstruction().toString().trim());
        if (!programNode.getParameters().isEmpty()) {
            stringBuilder.append(' ');
            for (ProgramNode parameter : programNode.getParameters()) {
                stringBuilder.append('\n');
                parameter.getType().formatter.format(parameter, stringBuilder, offset + 2);
            }
        }
    }),
    JUMP_DESTINATION((programNode, stringBuilder, offset) -> {
        stringBuilder.append(String.format("%06d │", programNode.getLine()));
        stringBuilder.append("[JUMP DESTINATION]");
    });

    @FunctionalInterface
    interface Formatter {
        void format(ProgramNode programNode, StringBuilder stringBuilder, int offset);
    }

    private Formatter formatter;

    ProgramNodeType(Formatter formatter) {
        this.formatter = formatter;
    }

    public String format(ProgramNode programNode) {
        StringBuilder stringBuilder = new StringBuilder();
        formatter.format(programNode, stringBuilder, 0);
        return stringBuilder.toString();
    }
}
