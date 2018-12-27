package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.edges.EdgeFlow
import com.hileco.cortex.analysis.edges.EdgeFlowMapping
import com.hileco.cortex.analysis.edges.EdgeFlowType
import java.util.*
import java.util.stream.Collectors

class FlowIterator constructor(private val edgeFlowMapping: EdgeFlowMapping,
                               private val edgeFlow: EdgeFlow = EdgeFlow(EdgeFlowType.START, null, 0),
                               private var children: List<FlowIterator>? = null,
                               private var counter: Int = 0) : Iterator<List<EdgeFlow>> {

    private fun initialize() {
        if (children == null) {
            var childrenEdgeFlows: Set<EdgeFlow>? = edgeFlowMapping.flowsFromSource[edgeFlow.target]
            if (childrenEdgeFlows == null || edgeFlow.target == null) {
                childrenEdgeFlows = emptySet()
            }
            children = childrenEdgeFlows.stream()
                    .map { childEdgeFlow -> FlowIterator(edgeFlowMapping, childEdgeFlow) }
                    .collect(Collectors.toList())
        }
    }

    private fun rotate(): Boolean {
        initialize()
        if (counter < children!!.size && !children!![counter].rotate()) {
            counter++
        }
        return hasNext()
    }

    private fun build(list: MutableList<EdgeFlow>) {
        list.add(edgeFlow)
        initialize()
        val totalChildren = children!!.size
        if (totalChildren > 0) {
            val activeChildIndex = counter % totalChildren
            val activeChild = children!![activeChildIndex]
            activeChild.build(list)
        }
    }

    override fun hasNext(): Boolean {
        initialize()
        return counter < children!!.size
    }

    override fun next(): List<EdgeFlow> {
        val list = ArrayList<EdgeFlow>()
        build(list)
        rotate()
        return list
    }

    fun reset() {
        initialize()
        counter = 0
        children!!.forEach { it.reset() }
    }
}
