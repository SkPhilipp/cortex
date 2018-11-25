package com.hileco.cortex.analysis.edges;

import com.hileco.cortex.analysis.Graph;
import com.hileco.cortex.analysis.GraphBlock;
import com.hileco.cortex.analysis.GraphNode;
import lombok.Value;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Value
public class EdgeUtility<T extends Edge> {
    private Class<T> edgeClass;

    public long count(GraphNode graphNode) {
        return graphNode.getEdges().stream()
                .filter(this.edgeClass::isInstance)
                .count();
    }

    @SuppressWarnings("unchecked")
    public Optional<T> findAny(GraphNode graphNode) {
        return (Optional<T>) graphNode.getEdges().stream()
                .filter(this.edgeClass::isInstance)
                .findAny();
    }

    @SuppressWarnings("unchecked")
    public Optional<T> findAny(Graph graph) {
        return (Optional<T>) graph.getEdges().stream()
                .filter(this.edgeClass::isInstance)
                .findAny();
    }

    @SuppressWarnings("unchecked")
    public Stream<T> filter(Edge edge) {
        return Stream.of(edge)
                .filter(this.edgeClass::isInstance)
                .map(filtered -> (T) filtered);
    }

    public void clear(Graph graph) {
        var edges = graph.getEdges();
        this.clear(edges);
        graph.getGraphBlocks().forEach(this::clear);
    }

    private void clear(GraphBlock graphBlock) {
        var edges = graphBlock.getEdges();
        this.clear(edges);
        graphBlock.getGraphNodes().forEach(this::clear);
    }

    private void clear(GraphNode graphNode) {
        var edges = graphNode.getEdges();
        this.clear(edges);
    }

    private void clear(List<Edge> edges) {
        for (var i = edges.size() - 1; i >= 0; i--) {
            if (this.edgeClass.isInstance(edges.get(i))) {
                edges.remove(i);
            }
        }
    }
}
