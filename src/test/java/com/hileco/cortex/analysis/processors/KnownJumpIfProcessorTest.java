package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.documentation.Documentation;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class KnownJumpIfProcessorTest extends ProcessorFuzzTest {

    @Test
    public void testProcess() {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new KnownJumpIfProcessor()

        ));
        var original = List.of(
                new PUSH(BigInteger.ONE.toByteArray()),
                new PUSH(BigInteger.TEN.toByteArray()),
                new JUMP_IF()
        );
        var graph = graphBuilder.build(original);
        var instructions = graph.toInstructions();

        Documentation.of(KnownJumpIfProcessor.class.getSimpleName())
                .headingParagraph(KnownJumpIfProcessor.class.getSimpleName())
                .paragraph("Replaces JUMP_IF instructions with JUMP or NOOP instructions where the condition is known ahead of time.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions);

        Assert.assertEquals(instructions, List.of(
                new NOOP(),
                new PUSH(BigInteger.TEN.toByteArray()),
                new JUMP()
        ));
    }

    @Override
    Processor fuzzTestableProcessor() {
        return new KnownJumpIfProcessor();
    }
}