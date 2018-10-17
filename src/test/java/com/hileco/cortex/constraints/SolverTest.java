package com.hileco.cortex.constraints;


import org.junit.Assert;
import org.junit.Test;

import static com.hileco.cortex.constraints.expressions.Operation2Expression.Type2.ADD;
import static com.hileco.cortex.constraints.expressions.Operation2Expression.Type2.LESS_THAN;
import static com.hileco.cortex.constraints.expressions.Operation2Expression.Type2.MODULO;
import static com.hileco.cortex.constraints.expressions.ReferenceExpression.ReferenceType.CALL_DATA;
import static com.hileco.cortex.constraints.expressions.ReferenceExpression.reference;
import static com.hileco.cortex.constraints.expressions.ValueExpression.value;

public class SolverTest {

    @Test
    public void test() {
        var expression = LESS_THAN.on(MODULO.on(ADD.on(reference(CALL_DATA, value(0L)), value(10L)), value(0xffffffL)), value(10L));
        var solver = new Solver();
        var solution = solver.solve(expression);
        var solutionInteger = solution.getPossibleValues().values().iterator().next();
        Assert.assertTrue((((solutionInteger + 10) % 16777215) < 10));
    }
}
