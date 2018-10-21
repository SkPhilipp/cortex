package com.hileco.cortex.constraints;

import com.hileco.cortex.context.data.ProgramStoreZone;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.math.SUBTRACT;
import com.hileco.cortex.instructions.stack.POP;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class ExpressionGeneratorTest {

    @Test
    public void testParameterized() {
        var builder = new ExpressionGenerator();
        builder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        builder.addInstruction(new SUBTRACT());
        Assert.assertEquals("(123 - 123)", builder.getCurrentExpression().toString());
    }

    @Test
    public void testPop() {
        var builder = new ExpressionGenerator();
        builder.addInstruction(new PUSH(BigInteger.valueOf(321L).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(1L).toByteArray()));
        builder.addInstruction(new POP());
        builder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(1L).toByteArray()));
        builder.addInstruction(new POP());
        builder.addInstruction(new SUBTRACT());
        Assert.assertEquals("(123 - 321)", builder.getCurrentExpression().toString());
    }

    @Test
    public void testReferences() {
        var builder = new ExpressionGenerator();
        builder.addInstruction(new PUSH(BigInteger.valueOf(10L).toByteArray()));
        builder.addInstruction(new LOAD(ProgramStoreZone.CALL_DATA));
        Assert.assertEquals("CALL_DATA[10]", builder.getCurrentExpression().toString());
    }

    @Test
    public void testMissing() {
        var builder = new ExpressionGenerator();
        builder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        builder.addInstruction(new SUBTRACT());
        Assert.assertEquals("(123 - STACK[0])", builder.getCurrentExpression().toString());
    }
}
