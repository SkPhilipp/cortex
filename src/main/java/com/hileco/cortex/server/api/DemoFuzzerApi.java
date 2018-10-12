package com.hileco.cortex.server.api;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.analysis.processors.Processor;
import com.hileco.cortex.fuzzer.ProgramGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.web.reactive.function.server.ServerResponse.status;

public class DemoFuzzerApi implements HandlerFunction<ServerResponse> {

    private static final String PARAM_SEED = "seed";

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        var seed = Long.parseLong(request.queryParam(PARAM_SEED).orElse("0"));
        List<Processor> processors = new ArrayList<>();
        processors.add(new ParameterProcessor());
        var graphBuilder = new GraphBuilder(processors);
        var programGenerator = new ProgramGenerator();
        var generated = programGenerator.generate(seed);
        var first = generated.keySet().iterator().next();
        var program = generated.get(first);
        var graph = graphBuilder.build(program.getInstructions());
        return status(HttpStatus.OK)
                .syncBody(Map.of("program", program.toString(),
                                 "graph", graph.toString()));
    }
}
