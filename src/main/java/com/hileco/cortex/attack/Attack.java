package com.hileco.cortex.attack;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.analysis.edges.EdgeFlowMapping;
import com.hileco.cortex.analysis.edges.EdgeFlowType;
import com.hileco.cortex.analysis.processors.ExitTrimProcessor;
import com.hileco.cortex.analysis.processors.FlowProcessor;
import com.hileco.cortex.analysis.processors.JumpIllegalProcessor;
import com.hileco.cortex.analysis.processors.KnownJumpIfProcessor;
import com.hileco.cortex.analysis.processors.KnownLoadProcessor;
import com.hileco.cortex.analysis.processors.KnownProcessor;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.pathing.FlowIterator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Attack {
    private static final GraphBuilder GRAPH_BUILDER = new GraphBuilder(List.of(
            new ParameterProcessor(),
            new FlowProcessor(),
            new ExitTrimProcessor(),
            new JumpIllegalProcessor(),
            new KnownJumpIfProcessor(),
            new KnownLoadProcessor(new HashMap<>(), new HashSet<>()),
            new KnownProcessor()
    ));

    private static final long FUZZER_SEED = 2;

    public static void main(String[] args) {
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(FUZZER_SEED);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var instructions = program.getInstructions();
        var graph = GRAPH_BUILDER.build(instructions);
        EdgeFlowMapping.UTIL.findAny(graph).ifPresent(edgeFlowMapping -> {
            var flowIterator = new FlowIterator(edgeFlowMapping);
            flowIterator.forEachRemaining(edgeFlows -> {
                var path = edgeFlows.stream()
                        .map(edgeFlow -> String.format(" (%s)--> %s", edgeFlow.getType(), edgeFlow.getTarget()))
                        .collect(Collectors.joining());
                System.out.println("---------------------------------------");
                System.out.println(path);
                System.out.println("---------------------------------------");
                edgeFlows.stream()
                        .filter(edgeFlow -> edgeFlow.getType() == EdgeFlowType.BLOCK_PART
                                || edgeFlow.getType() == EdgeFlowType.BLOCK_END
                                || edgeFlow.getType() == EdgeFlowType.END)
                        .map(edgeFlow -> String.format("PARTS FROM %s TO %s", edgeFlow.getSource(), edgeFlow.getTarget()))
                        .forEach(System.out::println);
                System.out.println();
                System.out.println();
                System.out.println();
            });
        });
    }

}
