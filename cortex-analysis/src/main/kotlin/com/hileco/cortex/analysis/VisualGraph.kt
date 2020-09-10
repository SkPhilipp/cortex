package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.edges.FlowMapping
import java.io.OutputStream
import java.util.*

class VisualGraph {
    private val visualGraphPrettifier = VisualGraphPrettifier()
    private val blocks: MutableMap<Int, String> = HashMap()
    private val jumpMapping: MutableMap<Int, MutableList<Int>> = HashMap()

    private fun map(graphBlock: GraphBlock) {
        val first = graphBlock.graphNodes.first().position
        val instructions = graphBlock.graphNodes.map { it.instruction }
        blocks[first] = visualGraphPrettifier.prettify(instructions).joinToString(separator = "\n")
    }

    private fun map(flowMapping: FlowMapping) {
        flowMapping.flowsFromSource.forEach { (source, flows) ->
            if (source != null) {
                flows.forEach { flow ->
                    if (flow.target != null && flow.type.jumps) {
                        val list = jumpMapping.getOrPut(source) { mutableListOf() }
                        list.add(flow.target)
                    }
                }
            }
        }
    }

    fun map(graph: Graph) {
        graph.graphBlocks.forEach { this.map(it) }
        graph.edgeMapping.get(FlowMapping::class.java).forEach {
            this.map(it)
        }
    }

    fun render(outputStream: OutputStream) {
        val writer = outputStream.writer()
        blocks.entries.sortedBy { it.key }.forEach { (address, block) ->
            writer.write("@$address\n$block\n\n")
        }
        jumpMapping.entries.sortedBy { it.key }.forEach { (source, targets) ->
            val targetsText = targets.sorted().joinToString { "@$it" }
            writer.write("@$source --> $targetsText\n")
        }
        writer.flush()
    }
}
