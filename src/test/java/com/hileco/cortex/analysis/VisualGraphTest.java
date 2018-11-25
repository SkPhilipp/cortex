package com.hileco.cortex.analysis;

import com.hileco.cortex.analysis.processors.TrimEndProcessor;
import com.hileco.cortex.analysis.processors.FlowProcessor;
import com.hileco.cortex.analysis.processors.JumpIllegalProcessor;
import com.hileco.cortex.analysis.processors.KnownJumpIfProcessor;
import com.hileco.cortex.analysis.processors.KnownLoadProcessor;
import com.hileco.cortex.analysis.processors.KnownProcessor;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.documentation.Documentation;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.Instruction;
import guru.nidi.graphviz.engine.Format;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class VisualGraphTest {

    private static final GraphBuilder GRAPH_BUILDER = new GraphBuilder(List.of(
            new ParameterProcessor(),
            new FlowProcessor(),
            new TrimEndProcessor(),
            new JumpIllegalProcessor(),
            new KnownJumpIfProcessor(),
            new KnownLoadProcessor(new HashMap<>()),
            new KnownProcessor()
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
        var graph = GRAPH_BUILDER.build(this.instructions);
        var visualGraph = new VisualGraph();
        visualGraph.map(graph);
        var outputStream = new ByteArrayOutputStream();
        visualGraph.getVizGraph().render(Format.PNG).toOutputStream(outputStream);
        Documentation.of(VisualGraph.class.getSimpleName())
                .headingParagraph(VisualGraph.class.getSimpleName())
                .paragraph("Program:").source(this.instructions)
                .paragraph("Visualization:").image(outputStream.toByteArray());
    }
}