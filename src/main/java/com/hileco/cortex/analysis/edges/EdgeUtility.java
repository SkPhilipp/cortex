package com.hileco.cortex.analysis.edges;

import com.hileco.cortex.analysis.GraphNode;
import lombok.Value;

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
    public Stream<T> filter(Edge edge) {
        return Stream.of(edge)
                .filter(this.edgeClass::isInstance)
                .map(filtered -> (T) filtered);
    }
}
