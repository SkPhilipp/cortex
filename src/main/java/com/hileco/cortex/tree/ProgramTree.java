package com.hileco.cortex.tree;

import com.hileco.cortex.instructions.Instruction;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class ProgramTree {

    // TODO: Trees are represented in memory as such:
    //
    //  line: load(call_data, 0x1200)
    //  tree:
    //  { type: [instruction], line: 1, operator: load, operands: [
    //    { type: [fixed], value: call_data },
    //    { type: [fixed], value: 0x1200 }
    //  ] }
    //
    //  line: add(load(call_data, 0x1200), load(call_data, 0x2400))
    //  tree:
    //  { type: [instruction], line: 3, operator: add, parameters: [
    //    { type: [instruction, parameter], line: 1, operator: load, operands: [
    //      { type: [fixed], value: call_data },
    //      { type: [fixed], value: 0x1200 }
    //    ] },
    //    { type: [instruction, parameter], line: 2, operator: load, operands: [
    //      { type: [fixed], value: call_data },
    //      { type: [fixed], value: 0x2400 }
    //    ] }
    //  ] }

    private final List<Instruction> instructions;
    private final List<ProgramNode> nodes;

    public ProgramTree(List<Instruction> instructions) {
        this.instructions = instructions;
        nodes = new ArrayList<>();
    }
}
