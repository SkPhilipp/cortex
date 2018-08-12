package com.hileco.cortex.tree;

import com.hileco.cortex.instructions.Instruction;
import lombok.Data;

import java.util.List;

@Data
public class ProgramNode {
    private ProgramNodeType type;
    private Instruction<?, ?> instruction;
    private Integer line;
    private Object value;
    private List<ProgramNode> parameters;

    @Override
    public String toString() {
        return type.format(this);
    }
}
