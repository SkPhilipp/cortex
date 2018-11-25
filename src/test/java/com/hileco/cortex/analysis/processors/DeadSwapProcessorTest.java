package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.documentation.Documentation;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.stack.PUSH;
import com.hileco.cortex.instructions.stack.SWAP;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class DeadSwapProcessorTest extends ProcessorFuzzTest {

    @Test
    public void testProcess() {
        var graphBuilder = new GraphBuilder(List.of(
                new DeadSwapProcessor()

        ));
        var original = List.of(
                new PUSH(BigInteger.ONE.toByteArray()),
                new PUSH(BigInteger.TEN.toByteArray()),
                new SWAP(0, 0)
        );
        var graph = graphBuilder.build(original);
        var instructions = graph.toInstructions();

        Documentation.of(DeadSwapProcessor.class.getSimpleName())
                .headingParagraph(DeadSwapProcessor.class.getSimpleName())
                .paragraph("Removes SWAP instructions which swap an element on the stack with the same element.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions);

        Assert.assertEquals(instructions, List.of(
                new PUSH(BigInteger.ONE.toByteArray()),
                new PUSH(BigInteger.TEN.toByteArray()),
                new NOOP()
        ));
    }

    @Override
    Processor fuzzTestableProcessor() {
        return new DeadSwapProcessor();
    }
}