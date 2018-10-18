package com.hileco.cortex.constraints;

import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.math.SUBTRACT;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

import static com.hileco.cortex.constraints.expressions.Operation2Expression.Type2.EQUAL_TO;
import static com.hileco.cortex.constraints.expressions.ValueExpression.value;

public class ExpressionBuilderTest {

    @Test
    public void testConstraintMidProcess() {
        var expressionBuilder = new ExpressionBuilder();
        var constraint = EQUAL_TO.on(null, value(0L));
        expressionBuilder.addInstruction(new JUMP_IF());
        expressionBuilder.addInstruction(new PUSH(BigInteger.valueOf(246L).toByteArray()));
        expressionBuilder.bind(0, constraint::setLeft);
        expressionBuilder.addInstruction(new SUBTRACT());
        expressionBuilder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        expressionBuilder.addInstruction(new NOOP());
        expressionBuilder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        var solver = new Solver();
        var solution = solver.solve(constraint);
        Assert.assertTrue(solution.isSolvable());
        Assert.assertEquals("((123 - 123) == 0)", constraint.toString());
    }
}
