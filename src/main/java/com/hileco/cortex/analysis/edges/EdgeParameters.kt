package com.hileco.cortex.analysis.edges

import com.hileco.cortex.analysis.GraphNode

class EdgeParameters(val graphNodes: List<GraphNode?>) : Edge {
    companion object {
        val UTIL = EdgeUtility(EdgeParameters::class.java)
    }
}
