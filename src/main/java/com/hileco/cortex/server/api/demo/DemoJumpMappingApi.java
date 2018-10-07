package com.hileco.cortex.server.api.demo;

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
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DemoJumpMappingApi implements Route {

    private static final String PARAM_SEED = "seed";

    @Override
    public Object handle(Request request, Response response) {
        var seed = Long.parseLong(request.queryParams(PARAM_SEED));
        var graphBuilder = new GraphBuilder(Arrays.asList(
                new ParameterProcessor(),
                new FlowProcessor(),
                new ExitTrimProcessor(),
                new JumpIllegalProcessor(),
                new KnownJumpIfProcessor(),
                new KnownLoadProcessor(new HashMap<>(), new HashSet<>()),
                new KnownProcessor(),
                new FlowProcessor()
        ));
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(seed);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var graph = graphBuilder.build(program.getInstructions());
        var jumpMapping = new ArrayList<String>();
        graph.getEdges().stream()
                .flatMap(EdgeFlowMapping.UTIL::filter)
                .forEach(edge -> edge.getJumpMapping().forEach((source, targets) -> {
                    var stringBuilder = new StringBuilder();
                    stringBuilder.append(String.format("%04d", source));
                    stringBuilder.append('\n');
                    targets.forEach(target -> {
                        stringBuilder.append(String.format(" --> %04d", target));
                        stringBuilder.append('\n');

                    });
                    jumpMapping.add(stringBuilder.toString());
                }));
        return Map.of("program", program.toString(),
                      "jumpMapping", jumpMapping);
    }
}
