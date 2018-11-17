package com.hileco.cortex.analysis.processors;

import com.hileco.cortex.analysis.GraphBuilder;
import com.hileco.cortex.analysis.edges.EdgeParameterConsumer;
import com.hileco.cortex.analysis.edges.EdgeParameters;
import com.hileco.cortex.vm.data.ProgramStoreZone;
import com.hileco.cortex.instructions.io.LOAD;
import com.hileco.cortex.instructions.jumps.JUMP_IF;
import com.hileco.cortex.instructions.stack.PUSH;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ParameterProcessorTest {

    @Test
    public void test() {
        var processors = new ArrayList<Processor>();
        processors.add(new ParameterProcessor());
        var graphBuilder = new GraphBuilder(processors);
        var graph = graphBuilder.build(List.of(
                new PUSH(BigInteger.ONE.toByteArray()),
                new PUSH(BigInteger.TEN.toByteArray()),
                new JUMP_IF()
        ));
        var graphBlocks = graph.getGraphBlocks();
        var graphNodes = graphBlocks.get(0).getGraphNodes();
        Assert.assertEquals(1L, EdgeParameterConsumer.UTIL.count(graphNodes.get(0)));
        Assert.assertEquals(1L, EdgeParameterConsumer.UTIL.count(graphNodes.get(1)));
        var edgeParametersOptional = EdgeParameters.UTIL.findAny(graphNodes.get(2));
        Assert.assertTrue(edgeParametersOptional.isPresent());
        Assert.assertEquals(2, edgeParametersOptional.get().getGraphNodes().size());
    }

    @Test
    public void testMultipleConsumers() {
        var processors = new ArrayList<Processor>();
        processors.add(new ParameterProcessor());
        var graphBuilder = new GraphBuilder(processors);
        var graph = graphBuilder.build(List.of(
                new PUSH(BigInteger.valueOf(2358L).toByteArray()),
                new LOAD(ProgramStoreZone.CALL_DATA),
                new PUSH(BigInteger.valueOf(7209L).toByteArray()),
                new LOAD(ProgramStoreZone.CALL_DATA)
        ));
        var graphBlocks = graph.getGraphBlocks();
        var graphNodes = graphBlocks.get(0).getGraphNodes();

    }
}