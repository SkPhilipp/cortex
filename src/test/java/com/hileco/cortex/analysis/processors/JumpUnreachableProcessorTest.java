package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.documentation.Documentation;
import com.hileco.cortex.instructions.debug.NOOP;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class JumpUnreachableProcessorTest extends ProcessorFuzzTest {

    @Test
    public void testProcessUnreachable() {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new FlowProcessor(),
                new JumpUnreachableProcessor()
        ));
        var original = List.of(
                new PUSH(BigInteger.valueOf(3).toByteArray()),
                new JUMP(),
                new JUMP_DESTINATION(),
                new JUMP_DESTINATION()
        );
        var graph = graphBuilder.build(original);
        var instructions = graph.toInstructions();

        Documentation.of(JumpUnreachableProcessor.class.getSimpleName())
                .headingParagraph(JumpUnreachableProcessor.class.getSimpleName())
                .paragraph("Eliminates instructions which are never jumped to.")
                .paragraph("Program before:").source(original)
                .paragraph("Program after:").source(instructions);

        Assert.assertEquals(instructions, List.of(
                new PUSH(BigInteger.valueOf(3).toByteArray()),
                new JUMP(),
                new NOOP(),
                new JUMP_DESTINATION()
        ));
    }

    @Test
    public void testProcessUntouched() {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new FlowProcessor(),
                new JumpUnreachableProcessor()
        ));
        var graph = graphBuilder.build(List.of(
                new PUSH(BigInteger.valueOf(2).toByteArray()),
                new JUMP(),
                new JUMP_DESTINATION(),
                new JUMP_DESTINATION()
        ));
        Assert.assertEquals(graph.toInstructions(), List.of(
                new PUSH(BigInteger.valueOf(2).toByteArray()),
                new JUMP(),
                new JUMP_DESTINATION(),
                new JUMP_DESTINATION()
        ));
    }

    @Override
    Processor fuzzTestableProcessor() {
        return new JumpUnreachableProcessor();
    }
}