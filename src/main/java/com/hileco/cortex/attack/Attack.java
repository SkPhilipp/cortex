package com.hileco.cortex.attack;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.analysis.edges.EdgeFlow;
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
import lombok.Value;

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

    @Value
    private static final class Flow {
        private int line;
        private EdgeFlow edgeFlow;
        private Instruction instruction;

        @Override
        public String toString() {
            return String.format("%04d | %04d --> %04d | %s",
                                 this.line,
                                 this.edgeFlow.getSource(),
                                 this.edgeFlow.getTarget(),
                                 this.instruction);
        }
    }

    private static final long FUZZER_SEED = 2;
    private static final Set<EdgeFlowType> BLOCK_TO_END_TYPES = Set.of(EdgeFlowType.BLOCK_PART, EdgeFlowType.BLOCK_END, EdgeFlowType.END);

    public static void main(String[] args) {
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(FUZZER_SEED);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var instructions = program.getInstructions();
        var graph = GRAPH_BUILDER.build(instructions);
        var edgeFlowMapping = EdgeFlowMapping.UTIL.findAny(graph).orElseThrow();
        var flowIterator = new FlowIterator(edgeFlowMapping);
        flowIterator.forEachRemaining(edgeFlows -> {
            if (containsTarget(instructions, edgeFlows)) {
                attackPath(instructions, edgeFlows);
            }
        });
    }

    private static boolean containsTarget(List<Instruction> instructions, List<EdgeFlow> edgeFlows) {
        return edgeFlows.stream()
                .filter(edgeFlow -> BLOCK_TO_END_TYPES.contains(edgeFlow.getType()))
                .anyMatch(edgeFlow -> {
                    var source = edgeFlow.getSource();
                    var target = Optional.ofNullable(edgeFlow.getTarget()).orElse(instructions.size() - 1);
                    return instructions.subList(source, target + 1)
                            .stream()
                            .anyMatch(instruction -> instruction instanceof CALL);
                });
    }

    private static void attackPath(List<Instruction> instructions, List<EdgeFlow> edgeFlows) {
        var path = edgeFlows.stream()
                .map(edgeFlow -> String.format("(%s)--> %s ", edgeFlow.getType(), edgeFlow.getTarget()))
                .collect(Collectors.joining());
        System.out.println(path);
        System.out.println("     |----------------");
        for (var i = 0; i < edgeFlows.size(); i++) {
            var edgeFlow = edgeFlows.get(i);
            var edgeFlowNextAvailable = i + 1 < edgeFlows.size();
            var edgeFlowNext = edgeFlowNextAvailable ? edgeFlows.get(i + 1) : null;
            if (BLOCK_TO_END_TYPES.contains(edgeFlow.getType())) {
                var source = edgeFlow.getSource();
                var target = Optional.ofNullable(edgeFlow.getTarget()).orElse(instructions.size() - 1);
                for (var j = source; j <= target; j++) {
                    var instruction = instructions.get(j);
                    var flow = new Flow(j, edgeFlow, instruction);
                    System.out.println(flow);
                    if (target.equals(j) && edgeFlowNextAvailable) {
                        System.out.println(String.format("     | %04d ~~> %04d | << USING %s >>",
                                                         edgeFlowNext.getSource(),
                                                         edgeFlowNext.getTarget(),
                                                         edgeFlowNext.getType()));
                    }
                }
            }
        }
        System.out.println("     |----------------");
        System.out.println();
    }
}
