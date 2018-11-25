package com.hileco.cortex.analysis;

import com.hileco.cortex.analysis.processors.DeadSwapProcessor;
import com.hileco.cortex.analysis.processors.FlowProcessor;
import com.hileco.cortex.analysis.processors.JumpIllegalProcessor;
import com.hileco.cortex.analysis.processors.JumpThreadingProcessor;
import com.hileco.cortex.analysis.processors.JumpUnreachableProcessor;
import com.hileco.cortex.analysis.processors.KnownJumpIfProcessor;
import com.hileco.cortex.analysis.processors.KnownLoadProcessor;
import com.hileco.cortex.analysis.processors.KnownProcessor;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.analysis.processors.TrimEndProcessor;
import com.hileco.cortex.documentation.Documentation;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.Instruction;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class VisualGraphTest {

    private static final GraphBuilder BASIC_GRAPH_BUILDER = new GraphBuilder(List.of(
            new ParameterProcessor(),
            new FlowProcessor()
    ));

    private static final GraphBuilder OPTIMIZED_GRAPH_BUILDER = new GraphBuilder(List.of(
            new ParameterProcessor(),
            new FlowProcessor(),
            new TrimEndProcessor(),
            new DeadSwapProcessor(),
            new JumpIllegalProcessor(),
            new JumpThreadingProcessor(),
            new JumpUnreachableProcessor(),
            new KnownJumpIfProcessor(),
            new KnownLoadProcessor(new HashMap<>()),
            new KnownProcessor(),
            new FlowProcessor()
    ));

    private List<Instruction> instructions;

    @Before
    public void setup() {
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(0);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        this.instructions = program.getInstructions();
    }

    @Test
    public void testMap() throws IOException {
        var basicGraph = BASIC_GRAPH_BUILDER.build(this.instructions);
        var basicGraphVisualized = new VisualGraph();
        basicGraphVisualized.map(basicGraph);
        var optimizedGraph = OPTIMIZED_GRAPH_BUILDER.build(this.instructions);
        var optimizedGraphVisualized = new VisualGraph();
        optimizedGraphVisualized.map(optimizedGraph);
        Documentation.of(VisualGraph.class.getSimpleName())
                .headingParagraph(VisualGraph.class.getSimpleName())
                .paragraph("Program:").source(this.instructions)
                .paragraph("Visualization: (As basic graph)").image(basicGraphVisualized.toBytes())
                .paragraph("Visualization: (As optimized graph)").image(optimizedGraphVisualized.toBytes());
    }
}