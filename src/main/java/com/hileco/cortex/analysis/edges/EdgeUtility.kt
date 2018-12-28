package com.hileco.cortex.analysis.edges

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphBlock
import com.hileco.cortex.analysis.GraphNode
import java.util.stream.Stream

class EdgeUtility<T : Edge>(private val edgeClass: Class<T>) {
    fun count(graphNode: GraphNode): Int {
        return graphNode.edges.asSequence()
                .filterIsInstance(edgeClass)
                .count()
    }

    fun findAny(graphNode: GraphNode): T? {
        return graphNode.edges.asSequence().filterIsInstance(edgeClass).firstOrNull()
    }

    fun findAny(graph: Graph): T? {
        return graph.edges.asSequence().filterIsInstance(edgeClass).firstOrNull()
    }

    fun filter(edge: T): Stream<T> {
        return Stream.of<T>(edge)
                .filter { edgeClass.isInstance(it) }
    }

    fun clear(graph: Graph) {
        val edges = graph.edges
        this.clear(edges)
        graph.graphBlocks.forEach { this.clear(it) }
    }

    private fun clear(graphBlock: GraphBlock) {
        val edges = graphBlock.edges
        this.clear(edges)
        graphBlock.graphNodes.forEach { this.clear(it) }
    }

    private fun clear(graphNode: GraphNode) {
        val edges = graphNode.edges
        this.clear(edges)
    }

    private fun clear(edges: MutableList<Edge>) {
        for (i in edges.indices.reversed()) {
            if (edgeClass.isInstance(edges[i])) {
                edges.removeAt(i)
            }
        }
    }
}
