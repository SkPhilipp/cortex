package com.hileco.cortex.server.api;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.analysis.edges.EdgeFlowMapping;
import com.hileco.cortex.analysis.processors.ExitTrimProcessor;
import com.hileco.cortex.analysis.processors.FlowProcessor;
import com.hileco.cortex.analysis.processors.JumpIllegalProcessor;
import com.hileco.cortex.analysis.processors.KnownJumpIfProcessor;
import com.hileco.cortex.analysis.processors.KnownLoadProcessor;
import com.hileco.cortex.analysis.processors.KnownProcessor;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.pathing.PathIterator;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.springframework.web.reactive.function.server.ServerResponse.status;

public class DemoPathingApi implements HandlerFunction<ServerResponse> {

    private static final String PARAM_SEED = "seed";

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        var seed = Long.parseLong(request.queryParam(PARAM_SEED).orElse("0"));
        var graphBuilder = new GraphBuilder(Arrays.asList(
                new ParameterProcessor(),
                new FlowProcessor(),
                new ExitTrimProcessor(),
                new JumpIllegalProcessor(),
                new KnownJumpIfProcessor(),
                new KnownLoadProcessor(new HashMap<>(), new HashSet<>()),
                new KnownProcessor()
        ));
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(seed);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var graph = graphBuilder.build(program.getInstructions());
        var edgeFlowMapping = graph.getEdges().stream()
                .flatMap(EdgeFlowMapping.UTIL::filter)
                .findAny().get();
        var pathIterator = new PathIterator(edgeFlowMapping, 1);
        var instructions = graph.toInstructions();
        var paths = new ArrayList<String>();
        pathIterator.forEachRemaining(integers -> {
            var stringBuilder = new StringBuilder();
            stringBuilder.append("        ┌───────────────────────────────────");
            stringBuilder.append('\n');
            for (var i = 0; i < integers.size() - 1; i++) {
                var current = integers.get(i);
                var next = integers.get(i + 1);
                var instruction = instructions.get(current);
                if (current == 1 || instruction instanceof JUMP_DESTINATION) {
                    stringBuilder.append(String.format("        │ %% %d through %d", current, next));
                    stringBuilder.append('\n');
                    for (int index = current; index <= next; index++) {
                        instruction = instructions.get(index);
                        stringBuilder.append(String.format(" %06d │ %s", index, instruction));
                        stringBuilder.append('\n');
                    }
                }
            }
            var current = integers.get(integers.size() - 1);
            var instruction = instructions.get(current);
            stringBuilder.append("        │ % ending block");
            stringBuilder.append('\n');
            stringBuilder.append(String.format(" %06d │ %s", current, instruction));
            stringBuilder.append('\n');
            stringBuilder.append("        └───────────────────────────────────");
            paths.add(stringBuilder.toString());
        });
        return status(HttpStatus.OK)
                .syncBody(Map.of("program", program.toString(),
                                 "paths", paths));
    }
}
