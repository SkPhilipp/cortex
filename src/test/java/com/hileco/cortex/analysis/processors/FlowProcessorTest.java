package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.analysis.edges.EdgeFlowMapping;
import com.hileco.cortex.documentation.Documentation;
import com.hileco.cortex.instructions.jumps.JUMP;
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

import static com.hileco.cortex.analysis.edges.EdgeFlowType.INSTRUCTION_JUMP;

public class FlowProcessorTest extends ProcessorFuzzTest {

    @Test
    public void process() {
        var graphBuilder = new GraphBuilder(List.of(
                new ParameterProcessor(),
                new FlowProcessor()
        ));
        var original = List.of(
                new PUSH(BigInteger.valueOf(3).toByteArray()),
                new JUMP(),
                new PUSH(BigInteger.valueOf(10).toByteArray()),
                new JUMP_DESTINATION(),
                new PUSH(BigInteger.valueOf(1).toByteArray()));
        var graph = graphBuilder.build(original);
        Documentation.of(FlowProcessor.class.getSimpleName())
                .headingParagraph(FlowProcessor.class.getSimpleName())
                .paragraph("Adds edges describing the program flow, this includes JUMP and JUMP_IFs where jump address information is known ahead of time.");

        var edgeFlowMapping = EdgeFlowMapping.UTIL.findAny(graph).orElseThrow();
        var fromInstruction1 = edgeFlowMapping.getFlowsFromSource().get(1).stream().findFirst().orElseThrow();
        var toInstruction3 = edgeFlowMapping.getFlowsToTarget().get(3).stream().findFirst().orElseThrow();
        Assert.assertEquals(fromInstruction1.getTarget(), (Integer) 3);
        Assert.assertEquals(fromInstruction1.getType(), INSTRUCTION_JUMP);
        Assert.assertEquals(toInstruction3.getTarget(), (Integer) 3);
        Assert.assertEquals(toInstruction3.getType(), INSTRUCTION_JUMP);
    }

    @Override
    Processor fuzzTestableProcessor() {
        return new FlowProcessor();
    }
}