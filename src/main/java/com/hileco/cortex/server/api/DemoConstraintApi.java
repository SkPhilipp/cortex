package com.hileco.cortex.server.api;

import com.hileco.cortex.constraints.Reference;
import com.hileco.cortex.constraints.Solver;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.hileco.cortex.constraints.Expression.Type.ADD;
import static com.hileco.cortex.constraints.Expression.Type.LESS_THAN;
import static com.hileco.cortex.constraints.Expression.Type.MODULO;
import static com.hileco.cortex.constraints.Expression.operation;
import static com.hileco.cortex.constraints.Expression.reference;
import static com.hileco.cortex.constraints.Expression.value;
import static com.hileco.cortex.constraints.Reference.Type.CALL_DATA;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

public class DemoConstraintApi implements HandlerFunction<ServerResponse> {

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        var expression = operation(LESS_THAN,
                                   operation(MODULO,
                                             operation(ADD,
                                                       reference(new Reference(CALL_DATA, 0L)),
                                                       value(10L)),
                                             value(0xffffffL)),
                                   value(10L));
        var solver = new Solver();
        return status(HttpStatus.OK)
                .syncBody(Map.of("expression", expression.toString(),
                                 "solution", solver.solve(expression).toString()));
    }
}
