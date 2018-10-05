package com.hileco.cortex.analysis;

public enum GraphNodeType {

    UNKNOWN((graphNode, stringBuilder, offset) -> {
        stringBuilder.append(" ?????? │");
        for (var i = 0; i < offset; i++) {
            stringBuilder.append(' ');
        }
        stringBuilder.append(' ');
        stringBuilder.append("????");
    }),
    INSTRUCTION((graphNode, stringBuilder, offset) -> {
        stringBuilder.append(String.format(" %06d │", graphNode.getLine()));
        for (var i = 0; i < offset; i++) {
            stringBuilder.append(' ');
        }
        stringBuilder.append(' ');
        stringBuilder.append(graphNode.getInstruction().toString().trim());
        if (!graphNode.getParameters().isEmpty()) {
            stringBuilder.append(' ');
            for (var parameter : graphNode.getParameters()) {
                stringBuilder.append('\n');
                parameter.getType().formatter.format(parameter, stringBuilder, offset + 2);
            }
        }
    });

    private final Formatter formatter;

    GraphNodeType(Formatter formatter) {
        this.formatter = formatter;
    }

    public String format(GraphNode graphNode) {
        var stringBuilder = new StringBuilder();
        this.formatter.format(graphNode, stringBuilder, 0);
        return stringBuilder.toString();
    }

    interface Formatter {
        void format(GraphNode graphNode, StringBuilder stringBuilder, int offset);
    }
}
