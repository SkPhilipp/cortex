package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class KnownJumpIfProcessorTest {

    @Test
    public void testProcess() {
        var processors = new ArrayList<Processor>();
        processors.add(new ParameterProcessor());
        processors.add(new KnownJumpIfProcessor());
        var graphBuilder = new GraphBuilder(processors);

        var processed = graphBuilder.build(Arrays.asList(
                new PUSH(BigInteger.ONE.toByteArray()),
                new PUSH(BigInteger.TEN.toByteArray()),
                new JUMP_IF()
        ));

        var expected = graphBuilder.build(Arrays.asList(
                new NOOP(),
                new PUSH(BigInteger.TEN.toByteArray()),
                new JUMP()
        ));

        Assert.assertEquals(expected.toInstructions(), processed.toInstructions());
    }
}