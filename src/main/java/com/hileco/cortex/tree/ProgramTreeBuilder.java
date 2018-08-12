package com.hileco.cortex.tree;

import com.hileco.cortex.instructions.Instruction;

import java.util.ArrayList;
import java.util.List;

public class ProgramTreeBuilder {

    private List<ProgramTreeBuildingStrategy> strategies;

    public ProgramTreeBuilder(List<ProgramTreeBuildingStrategy> strategies) {
        this.strategies = strategies;
    }

    // TODO: Trees are represented in memory as such:
    //
    //  line: load(call_data, 0x1200)
    //
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

    public ProgramTree build(List<Instruction> instructions) {
        List<ProgramNode> programNodes = new ArrayList<>();
        for (int i = 0; i < instructions.size(); i++) {
            Instruction instruction = instructions.get(i);
            ProgramNode programNode = new ProgramNode();
            programNode.setLine(i);
            programNode.setType(ProgramNodeType.INSTRUCTION);
            programNode.setInstruction(instruction);
            programNodes.add(programNode);
        }
        ProgramTree programTree = new ProgramTree(programNodes);
        strategies.forEach(strategy -> strategy.expand(programTree));
        return programTree;
    }
}
