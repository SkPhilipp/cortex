package com.hileco.cortex.analysis.attack;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.analysis.GraphNode;
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
import com.hileco.cortex.constraints.Solution;
import com.hileco.cortex.constraints.Solver;
import com.hileco.cortex.constraints.expressions.ImpossibleExpressionException;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.ProgramException;
import com.hileco.cortex.instructions.calls.CALL;
import com.hileco.cortex.instructions.debug.HALT;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Value
public class Attacker {

    private static final Set<EdgeFlowType> BLOCK_TO_END_TYPES = Set.of(EdgeFlowType.BLOCK_PART, EdgeFlowType.BLOCK_END, EdgeFlowType.END);
    private static final GraphBuilder GRAPH_BUILDER = new GraphBuilder(List.of(
            new ParameterProcessor(),
            new FlowProcessor(),
            new ExitTrimProcessor(),
            new JumpIllegalProcessor(),
            new KnownJumpIfProcessor(),
            new KnownLoadProcessor(new HashMap<>()),
            new KnownProcessor()
    ));

    public static final Predicate<GraphNode> TARGET_IS_CALL = graphNode -> graphNode.getInstruction().get() instanceof CALL;
    public static final Predicate<GraphNode> TARGET_IS_HALT_WINNER = graphNode -> {
        var instruction = graphNode.getInstruction().get();
        return instruction instanceof HALT && ((HALT) instruction).getReason() == ProgramException.Reason.WINNER;
    };

    private Predicate<GraphNode> targetPredicate;

    public ArrayList<Solution> solve(Graph graph) {
        var solutions = new ArrayList<Solution>();
        var instructions = graph.toInstructions();
        EdgeFlowMapping.UTIL.findAny(graph).ifPresent(edgeFlowMapping -> {
            var flowIterator = new FlowIterator(edgeFlowMapping);
            flowIterator.forEachRemaining(edgeFlows -> {
                if (this.isTargeted(edgeFlows, instructions, edgeFlowMapping)) {
                    var attackPath = new AttackPath(instructions, edgeFlows);
                    var solver = new Solver();
                    try {
                        solutions.add(solver.solve(attackPath.toExpression()));
                    } catch (ImpossibleExpressionException ignored) {
                    }
                }
            });
        });
        return solutions;
    }

    private boolean isTargeted(List<EdgeFlow> edgeFlows, List<Instruction> instructions, EdgeFlowMapping edgeFlowMapping) {
        return edgeFlows.stream()
                .filter(edgeFlow -> BLOCK_TO_END_TYPES.contains(edgeFlow.getType()))
                .anyMatch(edgeFlow -> {
                    var source = edgeFlow.getSource();
                    var target = Optional.ofNullable(edgeFlow.getTarget()).orElse(instructions.size() - 1);
                    return IntStream.range(source, target + 1)
                            .mapToObj(line -> edgeFlowMapping.getNodeLineMapping().get(line))
                            .anyMatch(this.targetPredicate);
                });
    }
}
