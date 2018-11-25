package com.hileco.cortex.analysis;

import com.hileco.cortex.analysis.edges.EdgeFlowMapping;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.RankDir;
import guru.nidi.graphviz.attribute.Records;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Compass;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.Node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static guru.nidi.graphviz.attribute.Records.rec;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static guru.nidi.graphviz.model.Factory.port;
import static guru.nidi.graphviz.model.Link.between;

public class VisualGraph {

    private final Map<Integer, Node> vizNodeMapping;
    private guru.nidi.graphviz.model.Graph vizGraph;

    public VisualGraph() {
        this.vizNodeMapping = new HashMap<>();
        this.vizGraph = graph()
                .directed()
                .graphAttr().with(RankDir.LEFT_TO_RIGHT);
    }

    private void map(GraphBlock graphBlock) {
        var records = new ArrayList<String>();
        var lines = new ArrayList<Integer>();
        for (var graphNode : graphBlock.getGraphNodes()) {
            lines.add(graphNode.getLine());
            records.add(rec(graphNode.getLine().toString(), String.format("%d: %s", graphNode.getLine(), graphNode.getInstruction().get().toString())));
        }
        var node = node(lines.iterator().next().toString())
                .with(Records.of(records.toArray(new String[0])))
                .with(Color.WHITE.fill());
        lines.forEach(line -> this.vizNodeMapping.put(line, node));
        this.vizGraph = this.vizGraph.with(node);
    }

    private void map(EdgeFlowMapping edgeFlowMapping) {
        var nodeMapping = new HashMap<Node, ArrayList<Link>>();
        edgeFlowMapping.getFlowsFromSource().forEach((source, flows) -> {
            if (source != null) {
                var sourceVizNode = this.vizNodeMapping.get(source);
                flows.forEach(flow -> {
                    if (flow.getTarget() != null) {
                        var vizLinkSources = nodeMapping.computeIfAbsent(sourceVizNode, ignored -> new ArrayList<>());
                        var targetVizNode = this.vizNodeMapping.get(flow.getTarget());
                        if (!sourceVizNode.equals(targetVizNode)) {
                            vizLinkSources.add(between(port(source.toString()), targetVizNode.port(flow.getTarget().toString(), Compass.WEST)));
                        }
                    }
                });
            }
        });
        nodeMapping.forEach((node, links) -> this.vizGraph = this.vizGraph.with(node.link(links.toArray(new Link[0]))));
    }

    public void map(Graph graph) {
        graph.getGraphBlocks().forEach(this::map);
        graph.getEdges().stream().flatMap(EdgeFlowMapping.UTIL::filter).forEach(this::map);
    }

    public byte[] toBytes() throws IOException {
        var graphViz = Graphviz.fromGraph(this.vizGraph);
        var outputStream = new ByteArrayOutputStream();
        graphViz.render(Format.PNG).toOutputStream(outputStream);
        return outputStream.toByteArray();
    }
}
