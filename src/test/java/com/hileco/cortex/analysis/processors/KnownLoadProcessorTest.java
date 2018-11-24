package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.documentation.Documentation;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hileco.cortex.vm.ProgramStoreZone.DISK;

public class KnownLoadProcessorTest extends ProcessorFuzzTest {

    @Test
    public void process() {
        var configuration = Map.of(DISK, Map.of(BigInteger.ONE, BigInteger.ZERO));
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new KnownLoadProcessor(configuration)
        ));
        var original = List.of(
                new PUSH(BigInteger.ONE.toByteArray()),
                new LOAD(DISK)
        );
        var graph = graphBuilder.build(original);
        var instructions = graph.toInstructions();
        Documentation.of(KnownLoadProcessor.class.getSimpleName())
                .headingParagraph(KnownLoadProcessor.class.getSimpleName())
                .paragraph("Replaces LOAD instructions which are known to always provide the same data.")
                .paragraph("In this example, the processor has been configured with the following data:").source(configuration)
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions);
        Assert.assertEquals(instructions, List.of(
                new NOOP(),
                new PUSH(BigInteger.ZERO.toByteArray())
        ));
    }

    @Override
    Processor fuzzTestableProcessor() {
        return new KnownLoadProcessor(new HashMap<>());
    }
}