package com.hileco.cortex.analysis;

import com.hileco.cortex.instructions.Instruction;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TreeNode {
    @Setter
    private TreeNodeType type;
    @Setter
    private Instruction instruction;
    @Setter
    private Integer line;
    private List<TreeNode> parameters;

    public TreeNode(Instruction instruction, Integer line, List<TreeNode> parameters) {
        type = TreeNodeType.INSTRUCTION;
        this.instruction = instruction;
        this.line = line;
        this.parameters = parameters;
    }

    public TreeNode() {
        parameters = new ArrayList<>();
    }

    public String toString() {
        return type.format(this);
    }
}
