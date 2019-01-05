package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.edges.EdgeFlowMapping
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
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class VisualGraph {
    private val vizNodeMapping: MutableMap<Int, Node> = HashMap()
    private var vizGraph: guru.nidi.graphviz.model.Graph = graph().directed().graphAttr().with(RankDir.LEFT_TO_RIGHT)

    private fun map(graphBlock: GraphBlock) {
        val records = ArrayList<String>()
        val lines = ArrayList<Int>()
        for (graphNode in graphBlock.graphNodes) {
            lines.add(graphNode.line)
            records.add(rec(graphNode.line.toString(), "${graphNode.line}: ${graphNode.instruction}"))
        }
        val node = node("${lines.first()}")
                .with(Records.of(*records.toTypedArray()))
                .with(Color.WHITE.fill())
        lines.forEach { line -> vizNodeMapping[line] = node }
        vizGraph = vizGraph.with(node)
    }

    private fun map(edgeFlowMapping: EdgeFlowMapping) {
        val nodeMapping = HashMap<Node, ArrayList<Link>>()
        edgeFlowMapping.flowsFromSource.forEach { source, flows ->
            if (source != null) {
                val sourceVizNode = vizNodeMapping[source]!!
                flows.forEach { flow ->
                    if (flow.target != null) {
                        val vizLinkSources = nodeMapping.computeIfAbsent(sourceVizNode) { ArrayList() }
                        val targetVizNode = vizNodeMapping[flow.target]!!
                        if (sourceVizNode != targetVizNode) {
                            vizLinkSources.add(between(port("$source"), targetVizNode.port(flow.target.toString(), Compass.WEST)))
                        }
                    }
                }
            }
        }
        nodeMapping.forEach { node, links -> vizGraph = vizGraph.with(node.link(*links.toTypedArray())) }
    }

    fun map(graph: Graph) {
        graph.graphBlocks.forEach { this.map(it) }
        graph.edgeMapping.get(EdgeFlowMapping::class.java).forEach {
            this.map(it)
        }
    }

    @Throws(IOException::class)
    fun toBytes(): ByteArray {
        val graphViz = Graphviz.fromGraph(vizGraph)
        val outputStream = ByteArrayOutputStream()
        graphViz.render(Format.PNG).toOutputStream(outputStream)
        return outputStream.toByteArray()
    }
}
