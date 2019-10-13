package com.hileco.cortex.analysis.edges

import com.hileco.cortex.analysis.GraphBlock
import com.hileco.cortex.analysis.GraphNode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class FlowMapping(val flowsFromSource: MutableMap<Int?, MutableSet<Flow>> = HashMap(),
                       val flowsToTarget: MutableMap<Int?, MutableSet<Flow>> = HashMap(),
                       val flows: MutableList<Flow> = ArrayList(),
                       val blockLineMapping: MutableMap<Int, GraphBlock> = HashMap(),
                       val nodeLineMapping: MutableMap<Int, GraphNode> = HashMap()) : Edge {
    fun putLineMapping(key: Int, value: GraphBlock) {
        blockLineMapping[key] = value
    }

    fun putLineMapping(key: Int, value: GraphNode) {
        nodeLineMapping[key] = value
    }

    fun map(flow: Flow) {
        flowsFromSource.computeIfAbsent(flow.source) { HashSet() }.add(flow)
        flowsToTarget.computeIfAbsent(flow.target) { HashSet() }.add(flow)
        flows.add(flow)
    }
}
