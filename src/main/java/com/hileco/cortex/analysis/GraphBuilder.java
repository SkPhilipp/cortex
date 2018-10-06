package com.hileco.cortex.analysis;

import com.hileco.cortex.analysis.processors.Processor;
import com.hileco.cortex.instructions.Instruction;
import lombok.Value;

import java.util.List;

@Value
public class GraphBuilder {
    private List<Processor> processors;

    public Graph build(List<Instruction> instructions) {
        var graph = new Graph(instructions);
        this.processors.forEach(processor -> processor.process(graph));
        return graph;
    }
}
