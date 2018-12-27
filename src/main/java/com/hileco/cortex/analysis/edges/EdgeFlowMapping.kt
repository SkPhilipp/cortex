package com.hileco.cortex.analysis.edges

import com.hileco.cortex.analysis.GraphBlock
import com.hileco.cortex.analysis.GraphNode
import java.util.*
import kotlin.collections.HashMap

class EdgeFlowMapping(internal val flowsFromSource: MutableMap<Int?, MutableSet<EdgeFlow>> = HashMap(),
                      internal val flowsToTarget: MutableMap<Int?, MutableSet<EdgeFlow>> = HashMap(),
                      private val blockLineMapping: MutableMap<Int, GraphBlock> = HashMap(),
                      internal val nodeLineMapping: MutableMap<Int, GraphNode> = HashMap()) : Edge {

    fun putLineMapping(key: Int, value: GraphBlock) {
        blockLineMapping[key] = value
    }

    fun putLineMapping(key: Int, value: GraphNode) {
        nodeLineMapping[key] = value
    }

    fun map(edgeFlow: EdgeFlow) {
        flowsFromSource.computeIfAbsent(edgeFlow.source) { HashSet() }.add(edgeFlow)
        flowsToTarget.computeIfAbsent(edgeFlow.target) { HashSet() }.add(edgeFlow)
    }

    companion object {
        val UTIL = EdgeUtility(EdgeFlowMapping::class.java)
    }
}
