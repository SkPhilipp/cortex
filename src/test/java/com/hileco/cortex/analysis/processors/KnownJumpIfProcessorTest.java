package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class KnownJumpIfProcessorTest {

    @Test
    public void testProcess() {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new KnownJumpIfProcessor()

        ));

        var processed = graphBuilder.build(List.of(
                new PUSH(BigInteger.ONE.toByteArray()),
                new PUSH(BigInteger.TEN.toByteArray()),
                new JUMP_IF()
        ));

        var expected = graphBuilder.build(List.of(
                new NOOP(),
                new PUSH(BigInteger.TEN.toByteArray()),
                new JUMP()
        ));

        Assert.assertEquals(expected.toInstructions(), processed.toInstructions());
    }
}