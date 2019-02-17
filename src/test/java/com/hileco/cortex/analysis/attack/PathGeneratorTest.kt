package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.analysis.edges.FlowMapping
import com.hileco.cortex.analysis.edges.FlowType
import org.junit.Assert
import org.junit.Test

class PathGeneratorTest {
    @Test
    fun testIterating() {
        val flowMapping = FlowMapping()
        flowMapping.map(Flow(FlowType.PROGRAM_FLOW, 0, 30))
        flowMapping.map(Flow(FlowType.INSTRUCTION_JUMP, 10, 1010))
        flowMapping.map(Flow(FlowType.INSTRUCTION_JUMP, 20, 1020))
        flowMapping.map(Flow(FlowType.INSTRUCTION_JUMP, 30, 1030))
        flowMapping.map(Flow(FlowType.PROGRAM_FLOW, 1010, 1019))
        flowMapping.map(Flow(FlowType.PROGRAM_END, 1019, null))
        flowMapping.map(Flow(FlowType.PROGRAM_FLOW, 1020, 1029))
        flowMapping.map(Flow(FlowType.PROGRAM_END, 1029, null))
        flowMapping.map(Flow(FlowType.PROGRAM_FLOW, 1030, 1039))
        flowMapping.map(Flow(FlowType.PROGRAM_END, 1039, null))
        val pathGenerator = PathGenerator(flowMapping)
        var counter = 0
        while (pathGenerator.currentPath().isNotEmpty()) {
            counter++
            pathGenerator.next()
        }
        Assert.assertEquals(3, counter)
    }

    @Test
    fun testReset() {
        val flowMapping = FlowMapping()
        flowMapping.map(Flow(FlowType.PROGRAM_FLOW, 0, 30))
        flowMapping.map(Flow(FlowType.INSTRUCTION_JUMP, 10, 1000))
        flowMapping.map(Flow(FlowType.INSTRUCTION_JUMP, 12, 1000))
        flowMapping.map(Flow(FlowType.INSTRUCTION_JUMP, 14, 1000))
        flowMapping.map(Flow(FlowType.INSTRUCTION_JUMP, 16, 1000))
        flowMapping.map(Flow(FlowType.INSTRUCTION_JUMP, 30, 1000))
        flowMapping.map(Flow(FlowType.PROGRAM_FLOW, 1000, 1030))
        flowMapping.map(Flow(FlowType.INSTRUCTION_JUMP, 1010, 2000))
        flowMapping.map(Flow(FlowType.INSTRUCTION_JUMP, 1012, 2000))
        flowMapping.map(Flow(FlowType.INSTRUCTION_JUMP, 1014, 2000))
        flowMapping.map(Flow(FlowType.INSTRUCTION_JUMP, 1016, 2000))
        flowMapping.map(Flow(FlowType.INSTRUCTION_JUMP, 1030, 2000))
        flowMapping.map(Flow(FlowType.PROGRAM_FLOW, 2000, 2001))
        flowMapping.map(Flow(FlowType.PROGRAM_END, 2001, null))
        val pathGenerator = PathGenerator(flowMapping)
        var counter = 0
        while (pathGenerator.currentPath().isNotEmpty()) {
            counter++
            pathGenerator.next()
        }
        Assert.assertEquals(25, counter)
    }
}