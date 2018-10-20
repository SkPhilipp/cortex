package com.hileco.cortex.constraints;

import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.math.SUBTRACT;
import com.hileco.cortex.instructions.stack.POP;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

import static com.hileco.cortex.constraints.expressions.Operation2Expression.Type2.EQUAL_TO;
import static com.hileco.cortex.constraints.expressions.ValueExpression.value;

@SuppressWarnings("SuspiciousMethodCalls")
public class ExpressionBuilderTest {

    @Test
    public void testParameterized() {
        var expressionBuilder = new ExpressionBuilder();
        var constraint = EQUAL_TO.on(null, value(0L));
        expressionBuilder.bindConstraint(-1, constraint::setLeft);
        expressionBuilder.addInstruction(new SUBTRACT());
        expressionBuilder.addInstruction(new PUSH(BigInteger.valueOf(1L).toByteArray()));
        expressionBuilder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        Assert.assertEquals("((123 - 123) == 0)", constraint.toString());
    }

    @Test
    public void testPop() {
        var expressionBuilder = new ExpressionBuilder();
        var constraint = EQUAL_TO.on(null, value(0L));
        expressionBuilder.bindConstraint(-1, constraint::setLeft);
        expressionBuilder.addInstruction(new SUBTRACT());
        expressionBuilder.addInstruction(new POP());
        expressionBuilder.addInstruction(new PUSH(BigInteger.valueOf(1L).toByteArray()));
        expressionBuilder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        expressionBuilder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        Assert.assertEquals("((123 - 123) == 0)", constraint.toString());
    }

    @Test
    public void testConstraintBetweenInstructions() {
        var expressionBuilder = new ExpressionBuilder();
        var constraint = EQUAL_TO.on(null, value(0L));
        expressionBuilder.addInstruction(new JUMP_IF());
        expressionBuilder.addInstruction(new PUSH(BigInteger.valueOf(246L).toByteArray()));
        expressionBuilder.bindConstraint(-1, constraint::setLeft);
        expressionBuilder.addInstruction(new SUBTRACT());
        expressionBuilder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        expressionBuilder.addInstruction(new NOOP());
        expressionBuilder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        Assert.assertEquals("((123 - 123) == 0)", constraint.toString());
    }

    @Test
    public void testReferences() {
        var expressionBuilder = new ExpressionBuilder();
        var constraint = EQUAL_TO.on(null, value(0L));
        expressionBuilder.bindConstraint(-1, constraint::setLeft);
        expressionBuilder.addInstruction(new LOAD(ProgramStoreZone.CALL_DATA));
        expressionBuilder.addInstruction(new PUSH(BigInteger.valueOf(10L).toByteArray()));
        Assert.assertEquals("(CALL_DATA[10] == 0)", constraint.toString());
    }
}
