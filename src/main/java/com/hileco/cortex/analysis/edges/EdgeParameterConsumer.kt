package com.hileco.cortex.analysis.edges

import com.hileco.cortex.analysis.GraphNode

class EdgeParameterConsumer(val graphNode: GraphNode) : Edge {
    companion object {
        val UTIL = EdgeUtility(EdgeParameterConsumer::class.java)
    }
}
