package com.hileco.cortex.analysis;

import com.hileco.cortex.analysis.processors.Processor;
import com.hileco.cortex.instructions.Instruction;
import lombok.Value;

import java.util.List;

@Value
public class TreeBuilder {
    private List<Processor> processors;

    public Tree build(List<Instruction> instructions) {
        Tree tree = new Tree();
        tree.include(instructions);
        processors.forEach(processor -> processor.process(tree));
        return tree;
    }
}
