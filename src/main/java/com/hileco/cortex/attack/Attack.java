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
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.calls.CALL;
import com.hileco.cortex.pathing.FlowIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private static final Set<EdgeFlowType> BLOCK_TO_END_TYPES = Set.of(EdgeFlowType.BLOCK_PART, EdgeFlowType.BLOCK_END, EdgeFlowType.END);

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
                        .map(edgeFlow -> String.format("(%s)--> %s ", edgeFlow.getType(), edgeFlow.getTarget()))
                        .collect(Collectors.joining());
                var pathInstructions = new ArrayList<Instruction>();
                edgeFlows.stream()
                        .filter(edgeFlow -> BLOCK_TO_END_TYPES.contains(edgeFlow.getType()))
                        .forEach(edgeFlow -> {
                            var source = edgeFlow.getSource();
                            var target = Optional.ofNullable(edgeFlow.getTarget()).orElse(instructions.size() - 1);
                            pathInstructions.addAll(instructions.subList(source, target + 1));
                        });
                var containsCall = pathInstructions.stream().anyMatch(instruction -> CALL.class.equals(instruction.getClass()));
                if (containsCall) {
                    System.out.println(path);
                }
            });
        });
    }
}
