package com.hileco.cortex.analysis.attack;

import com.hileco.cortex.analysis.edges.EdgeFlow;
import com.hileco.cortex.analysis.edges.EdgeFlowMapping;
import com.hileco.cortex.analysis.edges.EdgeFlowType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class FlowIterator implements Iterator<List<EdgeFlow>> {
    private final EdgeFlowMapping edgeFlowMapping;
    private final EdgeFlow edgeFlow;
    private List<FlowIterator> children;
    private int counter;

    public FlowIterator(EdgeFlowMapping edgeFlowMapping, EdgeFlow edgeFlow) {
        this.edgeFlowMapping = edgeFlowMapping;
        this.edgeFlow = edgeFlow;
        this.children = null;
        this.counter = 0;
    }

    public FlowIterator(EdgeFlowMapping edgeFlowMapping) {
        this(edgeFlowMapping, new EdgeFlow(EdgeFlowType.START, null, 0));
    }

    private void initialize() {
        if (this.children == null) {
            var childrenEdgeFlows = this.edgeFlowMapping.getFlowsFromSource().get(this.edgeFlow.getTarget());
            if (childrenEdgeFlows == null || this.edgeFlow.getTarget() == null) {
                childrenEdgeFlows = Collections.emptySet();
            }
            this.children = childrenEdgeFlows.stream()
                    .map(childEdgeFlow -> new FlowIterator(this.edgeFlowMapping, childEdgeFlow))
                    .collect(Collectors.toList());
        }
    }

    private boolean rotate() {
        this.initialize();
        if (this.counter < this.children.size() && !this.children.get(this.counter).rotate()) {
            this.counter++;
        }
        return this.hasNext();
    }

    private void build(List<EdgeFlow> list) {
        list.add(this.edgeFlow);
        this.initialize();
        var totalChildren = this.children.size();
        if (totalChildren > 0) {
            var activeChildIndex = this.counter % totalChildren;
            var activeChild = this.children.get(activeChildIndex);
            activeChild.build(list);
        }
    }

    @Override
    public boolean hasNext() {
        this.initialize();
        return this.counter < this.children.size();
    }

    @Override
    public List<EdgeFlow> next() {
        var list = new ArrayList<EdgeFlow>();
        this.build(list);
        this.rotate();
        return list;
    }

    public void reset() {
        this.initialize();
        this.counter = 0;
        this.children.forEach(FlowIterator::reset);
    }
}
