package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.edges.FlowMapping
import guru.nidi.graphviz.attribute.Color
import guru.nidi.graphviz.attribute.RankDir
import guru.nidi.graphviz.attribute.Records
import guru.nidi.graphviz.attribute.Records.rec
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.model.Compass
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.model.Link
import guru.nidi.graphviz.model.Link.between
import guru.nidi.graphviz.model.Node
import java.io.FilterOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class VisualGraph {
    private val vizNodeMapping: MutableMap<Int, Node> = HashMap()
    private var vizGraph: guru.nidi.graphviz.model.Graph = graph().directed().graphAttr().with(RankDir.LEFT_TO_RIGHT)

    private fun map(graphBlock: GraphBlock) {
        val records = ArrayList<String>()
        val positions = ArrayList<Int>()
        for (graphNode in graphBlock.graphNodes) {
            positions.add(graphNode.position)
            records.add(rec(graphNode.position.toString(), "${graphNode.position}: ${graphNode.instruction}"))
        }
        val node = node("${positions.first()}")
                .with(Records.of(*records.toTypedArray()))
                .with(Color.WHITE.fill())
        positions.forEach { position -> vizNodeMapping[position] = node }
        vizGraph = vizGraph.with(node)
    }

    private fun map(flowMapping: FlowMapping) {
        val nodeMapping = HashMap<Node, ArrayList<Link>>()
        flowMapping.flowsFromSource.forEach { (source, flows) ->
            if (source != null) {
                val sourceVizNode = vizNodeMapping[source]!!
                flows.forEach { flow ->
                    if (flow.target != null && flow.type.jumps) {
                        val vizLinkSources = nodeMapping.computeIfAbsent(sourceVizNode) { ArrayList() }
                        val targetVizNode = vizNodeMapping[flow.target]
                        if (targetVizNode != null) {
                            vizLinkSources.add(between(port("$source"), targetVizNode.port(flow.target.toString(), Compass.WEST)))
                        }
                    }
                }
            }
        }
        nodeMapping.forEach { (node, links) -> vizGraph = vizGraph.with(node.link(*links.toTypedArray())) }
    }

    fun map(graph: Graph) {
        graph.graphBlocks.forEach { this.map(it) }
        graph.edgeMapping.get(FlowMapping::class.java).forEach {
            this.map(it)
        }
    }

    fun render(outputStream: OutputStream) {
        val graphViz = Graphviz.fromGraph(vizGraph)
        val closeProtectedStream = object : FilterOutputStream(outputStream) {
            @Throws(IOException::class)
            override fun close() {
            }
        }
        graphViz.render(Format.SVG).toOutputStream(closeProtectedStream)
    }
}
