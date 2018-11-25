package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.documentation.Documentation;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.jumps.EXIT;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class TrimEndProcessorTest extends ProcessorFuzzTest {

    @Test
    public void process() {
        var graphBuilder = new GraphBuilder(List.of(
                new TrimEndProcessor()

        ));
        var original = List.of(
                new EXIT(),
                new PUSH(BigInteger.TEN.toByteArray()),
                new PUSH(BigInteger.ONE.toByteArray()),
                new JUMP_DESTINATION(),
                new PUSH(BigInteger.TEN.toByteArray()),
                new PUSH(BigInteger.ONE.toByteArray())
        );
        var graph = graphBuilder.build(original);
        var instructions = graph.toInstructions();

        Documentation.of(TrimEndProcessor.class.getSimpleName())
                .headingParagraph(TrimEndProcessor.class.getSimpleName())
                .paragraph("Removes any instructions within the same jump-reachable block following another instruction that guarantees the instructions will" +
                                   " not be reached.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions);

        Assert.assertEquals(instructions, List.of(
                new EXIT(),
                new NOOP(),
                new NOOP(),
                new JUMP_DESTINATION(),
                new PUSH(BigInteger.TEN.toByteArray()),
                new PUSH(BigInteger.ONE.toByteArray())
        ));
    }

    @Override
    Processor fuzzTestableProcessor() {
        return new TrimEndProcessor();
    }
}