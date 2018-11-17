package com.hileco.cortex.attack;

import com.hileco.cortex.analysis.edges.EdgeFlow;
import com.hileco.cortex.analysis.edges.EdgeFlowMapping;
import com.hileco.cortex.analysis.edges.EdgeFlowType;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class FlowIteratorTest {

    @Test
    public void testIterating() {
        var edgeFlowMapping = new EdgeFlowMapping();
        edgeFlowMapping.map(new EdgeFlow(null, 0, 10));
        edgeFlowMapping.map(new EdgeFlow(null, 0, 20));
        edgeFlowMapping.map(new EdgeFlow(null, 0, 30));
        edgeFlowMapping.map(new EdgeFlow(null, 10, 1010));
        edgeFlowMapping.map(new EdgeFlow(null, 10, 1020));
        edgeFlowMapping.map(new EdgeFlow(null, 10, 1030));
        edgeFlowMapping.map(new EdgeFlow(null, 20, 2010));
        edgeFlowMapping.map(new EdgeFlow(null, 30, 3010));
        edgeFlowMapping.map(new EdgeFlow(null, 2010, 30));
        edgeFlowMapping.map(new EdgeFlow(null, 1030, 103010));
        var flowIterator = new FlowIterator(edgeFlowMapping, new EdgeFlow(EdgeFlowType.START, null, 0));
        var counter = new AtomicInteger(0);
        flowIterator.forEachRemaining(edgeFlows -> counter.incrementAndGet());
        Assert.assertEquals(5, counter.get());
    }

    @Test
    public void testReset() {
        var edgeFlowMapping = new EdgeFlowMapping();
        edgeFlowMapping.map(new EdgeFlow(null, 0, 10));
        edgeFlowMapping.map(new EdgeFlow(null, 0, 20));
        edgeFlowMapping.map(new EdgeFlow(null, 0, 30));
        edgeFlowMapping.map(new EdgeFlow(null, 10, 1010));
        edgeFlowMapping.map(new EdgeFlow(null, 10, 1020));
        edgeFlowMapping.map(new EdgeFlow(null, 10, 1030));
        edgeFlowMapping.map(new EdgeFlow(null, 20, 2010));
        edgeFlowMapping.map(new EdgeFlow(null, 30, 3010));
        edgeFlowMapping.map(new EdgeFlow(null, 2010, 30));
        edgeFlowMapping.map(new EdgeFlow(null, 1030, 103010));
        var flowIterator = new FlowIterator(edgeFlowMapping, new EdgeFlow(EdgeFlowType.START, null, 0));
        var counter = new AtomicInteger(0);
        flowIterator.forEachRemaining(edgeFlows -> counter.incrementAndGet());
        flowIterator.reset();
        flowIterator.forEachRemaining(edgeFlows -> counter.incrementAndGet());
        Assert.assertEquals(10, counter.get());
    }
}