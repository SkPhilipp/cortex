package com.hileco.cortex.server.api.demo;

import com.hileco.cortex.constraints.Reference;
import com.hileco.cortex.constraints.Solver;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

import static com.hileco.cortex.constraints.Expression.Type.ADD;
import static com.hileco.cortex.constraints.Expression.Type.LESS_THAN;
import static com.hileco.cortex.constraints.Expression.Type.MODULO;
import static com.hileco.cortex.constraints.Expression.operation;
import static com.hileco.cortex.constraints.Expression.reference;
import static com.hileco.cortex.constraints.Expression.value;
import static com.hileco.cortex.constraints.Reference.Type.CALL_DATA;

public class DemoConstraintApi implements Route {

    @Override
    public Object handle(Request request, Response response) {
        var expression = operation(LESS_THAN,
                                   operation(MODULO,
                                             operation(ADD,
                                                       reference(new Reference(CALL_DATA, 0L)),
                                                       value(10L)),
                                             value(0xffffffL)),
                                   value(10L));
        var solver = new Solver();
        return Map.of("expression", expression.toString(),
                      "solution", solver.solve(expression).toString());
    }
}
