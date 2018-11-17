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
import com.hileco.cortex.constraints.ExpressionGenerator;
import com.hileco.cortex.constraints.expressions.Expression;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.instructions.calls.CALL;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.pathing.FlowIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        var edgeFlowMapping = EdgeFlowMapping.UTIL.findAny(graph).orElseThrow();
        var flowIterator = new FlowIterator(edgeFlowMapping);
        flowIterator.forEachRemaining(edgeFlows -> {
            if (containsTarget(instructions, edgeFlows)) {
                var expression = attackPath(instructions, edgeFlows);
                System.out.println(expression);
            }
        });
        System.out.flush();
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

    private static Expression attackPath(List<Instruction> instructions, List<EdgeFlow> edgeFlows) {
        var expressionGenerator = new ExpressionGenerator();
        var conditions = new ArrayList<Expression>();
        for (var i = 0; i < edgeFlows.size(); i++) {
            var edgeFlow = edgeFlows.get(i);
            var edgeFlowNextAvailable = i + 1 < edgeFlows.size();
            var edgeFlowNext = edgeFlowNextAvailable ? edgeFlows.get(i + 1) : null;
            if (BLOCK_TO_END_TYPES.contains(edgeFlow.getType())) {
                var source = edgeFlow.getSource();
                var target = Optional.ofNullable(edgeFlow.getTarget()).orElse(instructions.size() - 1);
                for (var j = source; j <= target; j++) {
                    var instruction = instructions.get(j);
                    if (instruction instanceof JUMP_IF) {
                        var isLastOfBlock = target.equals(j);
                        var isNextBlockJumpIf = edgeFlowNextAvailable && EdgeFlowType.INSTRUCTION_JUMP_IF == edgeFlowNext.getType();
                        var expression = expressionGenerator.viewExpression(JUMP_IF.CONDITION.getPosition());
                        if (!(isLastOfBlock && isNextBlockJumpIf)) {
                            expression = new Expression.Not(expression);
                        }
                        conditions.add(expression);
                    }
                    expressionGenerator.addInstruction(instruction);
                }
            }
        }
        return new Expression.And(conditions);
    }
}
