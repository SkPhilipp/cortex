package com.hileco.cortex.demo;

import com.hileco.cortex.constraints.Reference;
import com.hileco.cortex.constraints.Solver;

import static com.hileco.cortex.constraints.Expression.Type.ADD;
import static com.hileco.cortex.constraints.Expression.Type.LESS_THAN;
import static com.hileco.cortex.constraints.Expression.Type.MODULO;
import static com.hileco.cortex.constraints.Expression.operation;
import static com.hileco.cortex.constraints.Expression.reference;
import static com.hileco.cortex.constraints.Expression.value;
import static com.hileco.cortex.constraints.Reference.Type.CALL_DATA;

class ConstraintDemo {

    /**
     * "Solve for ((call_data[0] + 10) % 0xffffff < 10)" implemented.
     */
    public static void main(String[] args) {
        var expression = operation(LESS_THAN,
                operation(MODULO,
                        operation(ADD,
                                reference(new Reference(CALL_DATA, 0L)),
                                value(10L)),
                        value(0xffffffL)),
                value(10L));
        var solver = new Solver();
        var solution = solver.solve(expression);
        System.out.println(solution);
    }
}
