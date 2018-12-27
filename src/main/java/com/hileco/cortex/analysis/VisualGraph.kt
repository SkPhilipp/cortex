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
import java.util.function.Consumer

class VisualGraph {

    private val vizNodeMapping: MutableMap<Int, Node>
    private var vizGraph: guru.nidi.graphviz.model.Graph

    init {
        vizNodeMapping = HashMap()
        vizGraph = graph()
                .directed()
                .graphAttr().with(RankDir.LEFT_TO_RIGHT)
    }

    private fun map(graphBlock: GraphBlock) {
        val records = ArrayList<String>()
        val lines = ArrayList<Int>()
        for (graphNode in graphBlock.graphNodes) {
            lines.add(graphNode.line)
            records.add(rec(graphNode.line.toString(), String.format("%d: %s", graphNode.line, graphNode.instruction.get().toString())))
        }
        val node = node(lines.iterator().next().toString())
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
                            vizLinkSources.add(between(port(source.toString()), targetVizNode.port(flow.target.toString(), Compass.WEST)))
                        }
                    }
                }
            }
        }
        nodeMapping.forEach { node, links -> vizGraph = vizGraph.with(node.link(*links.toTypedArray())) }
    }

    fun map(graph: Graph) {
        graph.graphBlocks.forEach(Consumer<GraphBlock> { this.map(it) })
        graph.edges.forEach {
            if (it is EdgeFlowMapping) {
                this.map(it)
            }
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
