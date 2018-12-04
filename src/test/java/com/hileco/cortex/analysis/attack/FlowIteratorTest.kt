package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.edges.EdgeFlow
import com.hileco.cortex.analysis.edges.EdgeFlowMapping
import com.hileco.cortex.analysis.edges.EdgeFlowType
import org.junit.Assert
import org.junit.Test

import java.util.concurrent.atomic.AtomicInteger

class FlowIteratorTest {

    @Test
    fun testIterating() {
        val edgeFlowMapping = EdgeFlowMapping()
        edgeFlowMapping.map(EdgeFlow(null, 0, 10))
        edgeFlowMapping.map(EdgeFlow(null, 0, 20))
        edgeFlowMapping.map(EdgeFlow(null, 0, 30))
        edgeFlowMapping.map(EdgeFlow(null, 10, 1010))
        edgeFlowMapping.map(EdgeFlow(null, 10, 1020))
        edgeFlowMapping.map(EdgeFlow(null, 10, 1030))
        edgeFlowMapping.map(EdgeFlow(null, 20, 2010))
        edgeFlowMapping.map(EdgeFlow(null, 30, 3010))
        edgeFlowMapping.map(EdgeFlow(null, 2010, 30))
        edgeFlowMapping.map(EdgeFlow(null, 1030, 103010))
        val flowIterator = FlowIterator(edgeFlowMapping, EdgeFlow(EdgeFlowType.START, null, 0))
        val counter = AtomicInteger(0)
        flowIterator.forEachRemaining { counter.incrementAndGet() }
        Assert.assertEquals(5, counter.get().toLong())
    }

    @Test
    fun testReset() {
        val edgeFlowMapping = EdgeFlowMapping()
        edgeFlowMapping.map(EdgeFlow(null, 0, 10))
        edgeFlowMapping.map(EdgeFlow(null, 0, 20))
        edgeFlowMapping.map(EdgeFlow(null, 0, 30))
        edgeFlowMapping.map(EdgeFlow(null, 10, 1010))
        edgeFlowMapping.map(EdgeFlow(null, 10, 1020))
        edgeFlowMapping.map(EdgeFlow(null, 10, 1030))
        edgeFlowMapping.map(EdgeFlow(null, 20, 2010))
        edgeFlowMapping.map(EdgeFlow(null, 30, 3010))
        edgeFlowMapping.map(EdgeFlow(null, 2010, 30))
        edgeFlowMapping.map(EdgeFlow(null, 1030, 103010))
        val flowIterator = FlowIterator(edgeFlowMapping, EdgeFlow(EdgeFlowType.START, null, 0))
        val counter = AtomicInteger(0)
        flowIterator.forEachRemaining { counter.incrementAndGet() }
        flowIterator.reset()
        flowIterator.forEachRemaining { counter.incrementAndGet() }
        Assert.assertEquals(10, counter.get().toLong())
    }
}