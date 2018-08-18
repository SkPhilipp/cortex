package com.hileco.cortex.analysis;

public enum TreeNodeType {

    UNKNOWN((treeNode, stringBuilder, offset) -> {
        stringBuilder.append(" ?????? │");
        for (int i = 0; i < offset; i++) {
            stringBuilder.append(' ');
        }
        stringBuilder.append(' ');
        stringBuilder.append("????");
    }),
    INSTRUCTION((treeNode, stringBuilder, offset) -> {
        stringBuilder.append(String.format(" %06d │", treeNode.getLine()));
        for (int i = 0; i < offset; i++) {
            stringBuilder.append(' ');
        }
        stringBuilder.append(' ');
        stringBuilder.append(treeNode.getInstruction().toString().trim());
        if (!treeNode.getParameters().isEmpty()) {
            stringBuilder.append(' ');
            for (TreeNode parameter : treeNode.getParameters()) {
                stringBuilder.append('\n');
                parameter.getType().formatter.format(parameter, stringBuilder, offset + 2);
            }
        }
    });

    interface Formatter {
        void format(TreeNode treeNode, StringBuilder stringBuilder, int offset);
    }

    private Formatter formatter;

    TreeNodeType(Formatter formatter) {
        this.formatter = formatter;
    }

    public String format(TreeNode treeNode) {
        StringBuilder stringBuilder = new StringBuilder();
        formatter.format(treeNode, stringBuilder, 0);
        return stringBuilder.toString();
    }
}
