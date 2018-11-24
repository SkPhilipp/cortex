package com.hileco.cortex.constraints;

import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.math.SUBTRACT;
import com.hileco.cortex.instructions.stack.DUPLICATE;
import com.hileco.cortex.instructions.stack.POP;
import com.hileco.cortex.instructions.stack.PUSH;
import com.hileco.cortex.instructions.stack.SWAP;
import com.hileco.cortex.vm.ProgramStoreZone;
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

    @Test
    public void testMultipleExpressions() {
        var builder = new ExpressionGenerator();
        builder.addInstruction(new PUSH(BigInteger.valueOf(456L).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(456L).toByteArray()));
        builder.addInstruction(new ADD());
        builder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        builder.addInstruction(new SUBTRACT());
        Assert.assertEquals("(123 - 123)", builder.viewExpression(0).toString());
        Assert.assertEquals("(456 + 456)", builder.viewExpression(1).toString());
    }

    @Test
    public void testDuplicate() {
        var builder = new ExpressionGenerator();
        builder.addInstruction(new PUSH(BigInteger.valueOf(456L).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(456L).toByteArray()));
        builder.addInstruction(new ADD());
        builder.addInstruction(new DUPLICATE(0));
        Assert.assertEquals("(456 + 456)", builder.viewExpression(0).toString());
        Assert.assertEquals("(456 + 456)", builder.viewExpression(1).toString());
    }

    @Test
    public void testSwap() {
        var builder = new ExpressionGenerator();
        builder.addInstruction(new PUSH(BigInteger.valueOf(456L).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(456L).toByteArray()));
        builder.addInstruction(new ADD());
        builder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        builder.addInstruction(new PUSH(BigInteger.valueOf(123L).toByteArray()));
        builder.addInstruction(new SUBTRACT());
        builder.addInstruction(new SWAP(0 ,1));
        Assert.assertEquals("(123 - 123)", builder.viewExpression(1).toString());
        Assert.assertEquals("(456 + 456)", builder.viewExpression(0).toString());
    }
}
