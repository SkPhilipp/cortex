package com.hileco.cortex.server.api;

import com.hileco.cortex.constraints.ExpressionGenerator;
import com.hileco.cortex.instructions.Instruction;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.status;

public class InstructionsConstraintsApi implements HandlerFunction<ServerResponse> {

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        var builder = new ExpressionGenerator();
        var map = request.bodyToFlux(Instruction.class)
                .buffer()
                .map(instructions -> {
                    instructions.forEach(builder::addInstruction);
                    return builder.getCurrentExpression().toString();
                });
        return status(HttpStatus.OK).body((Publisher) map, Instruction.class);
    }
}
