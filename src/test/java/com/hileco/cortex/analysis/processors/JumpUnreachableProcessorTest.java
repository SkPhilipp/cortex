package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class JumpUnreachableProcessorTest {

    @Test
    public void testProcessUnreachable() {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new FlowProcessor(),
                new JumpUnreachableProcessor()
        ));

        var processed = graphBuilder.build(List.of(
                new PUSH(BigInteger.valueOf(3).toByteArray()),
                new JUMP(),
                new JUMP_DESTINATION(),
                new JUMP_DESTINATION()
        ));

        var expected = graphBuilder.build(List.of(
                new PUSH(BigInteger.valueOf(3).toByteArray()),
                new JUMP(),
                new NOOP(),
                new JUMP_DESTINATION()
        ));

        Assert.assertEquals(expected.toInstructions(), processed.toInstructions());
    }

    @Test
    public void testProcessUntouched() {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new FlowProcessor(),
                new JumpUnreachableProcessor()
        ));

        var processed = graphBuilder.build(List.of(
                new PUSH(BigInteger.valueOf(2).toByteArray()),
                new JUMP(),
                new JUMP_DESTINATION(),
                new JUMP_DESTINATION()
        ));

        var expected = graphBuilder.build(List.of(
                new PUSH(BigInteger.valueOf(2).toByteArray()),
                new JUMP(),
                new JUMP_DESTINATION(),
                new JUMP_DESTINATION()
        ));

        Assert.assertEquals(expected.toInstructions(), processed.toInstructions());
    }
}