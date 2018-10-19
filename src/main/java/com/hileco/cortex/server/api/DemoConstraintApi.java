package com.hileco.cortex.server.api;

import com.hileco.cortex.constraints.Solver;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.hileco.cortex.constraints.expressions.Operation2Expression.Type2.ADD;
import static com.hileco.cortex.constraints.expressions.Operation2Expression.Type2.LESS_THAN;
import static com.hileco.cortex.constraints.expressions.Operation2Expression.Type2.MODULO;
import static com.hileco.cortex.constraints.expressions.ReferenceExpression.reference;
import static com.hileco.cortex.constraints.expressions.ValueExpression.value;
import static com.hileco.cortex.context.data.ProgramStoreZone.CALL_DATA;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

public class DemoConstraintApi implements HandlerFunction<ServerResponse> {

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        var expression = LESS_THAN.on(MODULO.on(ADD.on(reference(CALL_DATA, value(0L)), value(10L)), value(0xffffffL)), value(10L));
        var solver = new Solver();
        return status(HttpStatus.OK)
                .syncBody(Map.of("expression", expression.toString(),
                                 "solution", solver.solve(expression).toString()));
    }
}
