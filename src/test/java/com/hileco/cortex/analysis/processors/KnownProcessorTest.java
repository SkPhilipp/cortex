package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.documentation.Documentation;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.math.ADD;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class KnownProcessorTest extends ProcessorFuzzTest {

    @Test
    public void process() {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new KnownProcessor()
        ));
        var original = List.of(
                new PUSH(BigInteger.ONE.toByteArray()),
                new PUSH(BigInteger.TEN.toByteArray()),
                new ADD()
        );
        var graph = graphBuilder.build(original);
        var instructions = graph.toInstructions();
        Documentation.of(KnownProcessor.class.getSimpleName())
                .headingParagraph(KnownProcessor.class.getSimpleName())
                .paragraph("Precomputes instructions which modify only the stack and do not have any external dependencies.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions);
        Assert.assertEquals(instructions, List.of(
                new NOOP(),
                new NOOP(),
                new PUSH(BigInteger.valueOf(11).toByteArray())
        ));
    }

    @Override
    Processor fuzzTestableProcessor() {
        return new KnownProcessor();
    }
}