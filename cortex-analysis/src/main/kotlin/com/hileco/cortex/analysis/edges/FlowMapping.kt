package com.hileco.cortex.analysis.edges

import com.hileco.cortex.analysis.GraphBlock
import com.hileco.cortex.analysis.GraphNode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class FlowMapping(val flowsFromSource: MutableMap<Int?, MutableSet<Flow>> = HashMap(),
                       val flowsToTarget: MutableMap<Int?, MutableSet<Flow>> = HashMap(),
                       val flows: MutableList<Flow> = ArrayList(),
                       val blockPositionMapping: MutableMap<Int, GraphBlock> = HashMap(),
                       val nodePositionMapping: MutableMap<Int, GraphNode> = HashMap()) : Edge {
    fun putPositionMapping(position: Int, value: GraphBlock) {
        blockPositionMapping[position] = value
    }

    fun putPositionMapping(position: Int, value: GraphNode) {
        nodePositionMapping[position] = value
    }

    fun map(flow: Flow) {
        flowsFromSource.computeIfAbsent(flow.source) { HashSet() }.add(flow)
        flowsToTarget.computeIfAbsent(flow.target) { HashSet() }.add(flow)
        flows.add(flow)
    }
}
