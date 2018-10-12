package com.hileco.cortex.server.api;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.analysis.processors.ExitTrimProcessor;
import com.hileco.cortex.analysis.processors.FlowProcessor;
import com.hileco.cortex.analysis.processors.JumpIllegalProcessor;
import com.hileco.cortex.analysis.processors.KnownJumpIfProcessor;
import com.hileco.cortex.analysis.processors.KnownLoadProcessor;
import com.hileco.cortex.analysis.processors.KnownProcessor;
import com.hileco.cortex.analysis.processors.ParameterProcessor;
import com.hileco.cortex.context.Program;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.MULTIPLY;
import com.hileco.cortex.instructions.stack.PUSH;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.springframework.web.reactive.function.server.ServerResponse.status;

public class DemoOptimizerApi implements HandlerFunction<ServerResponse> {

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        var graphBuilder = new GraphBuilder(Arrays.asList(
                new ParameterProcessor()
        ));
        var optimizedGraphBuilder = new GraphBuilder(Arrays.asList(
                new ParameterProcessor(),
                new FlowProcessor(),
                new ExitTrimProcessor(),
                new JumpIllegalProcessor(),
                new KnownJumpIfProcessor(),
                new KnownLoadProcessor(new HashMap<>(), new HashSet<>()),
                new KnownProcessor()
        ));
        var program = new Program(Arrays.asList(
                new PUSH(BigInteger.valueOf(1234L).toByteArray()),
                new PUSH(BigInteger.valueOf(5678L).toByteArray()),
                new ADD(),
                new PUSH(BigInteger.valueOf(2L).toByteArray()),
                new MULTIPLY()
        ));
        var graph = graphBuilder.build(program.getInstructions());
        var optimizedGraph = optimizedGraphBuilder.build(program.getInstructions());
        return status(HttpStatus.OK)
                .syncBody(Map.of("program", program.toString(),
                                 "graph", graph.toString(),
                                 "optimizedGraph", optimizedGraph.toString()));
    }
}
