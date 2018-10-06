package com.hileco.cortex.analysis.edges;

import com.hileco.cortex.analysis.GraphNode;
import lombok.Value;

import java.util.Optional;

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
}
