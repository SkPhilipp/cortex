package com.hileco.cortex.server.api;

import com.hileco.cortex.constraints.Solver;
import com.hileco.cortex.constraints.ExpressionGenerator;
import com.hileco.cortex.instructions.conditions.LESS_THAN;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.MODULO;
import com.hileco.cortex.instructions.stack.PUSH;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Map;

import static com.hileco.cortex.context.data.ProgramStoreZone.CALL_DATA;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

public class DemoConstraintApi implements HandlerFunction<ServerResponse> {

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        var builder = new ExpressionGenerator();
        builder.addInstruction(new PUSH(BigInteger.valueOf(10L).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(0xffffffL).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(10L).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(0L).toByteArray()));
        builder.addInstruction(new LOAD(CALL_DATA));
        builder.addInstruction(new ADD());
        builder.addInstruction(new MODULO());
        builder.addInstruction(new LESS_THAN());
        var solver = new Solver();
        return status(HttpStatus.OK)
                .syncBody(Map.of("expression", builder.getCurrentExpression().toString(),
                                 "solution", solver.solve(builder.getCurrentExpression()).toString()));
    }
}
