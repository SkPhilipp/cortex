package com.hileco.cortex.tree;

import com.hileco.cortex.instructions.Operations.Operation;

import java.util.List;
import java.util.Set;

public class ProgramNode<T extends Operation<O>, O> {
    private Set<ProgramNodeType> nodeTypes;
    private T operation;
    private O operands;
    private Integer line;
    private Object value;
    private List<ProgramNode> parameters;

    // TODO: Indicate optionals
    public Set<ProgramNodeType> getNodeTypes() {
        return nodeTypes;
    }

    public void setNodeTypes(Set<ProgramNodeType> nodeTypes) {
        this.nodeTypes = nodeTypes;
    }

    public T getOperation() {
        return operation;
    }

    public void setOperation(T operation) {
        this.operation = operation;
    }

    public O getOperands() {
        return operands;
    }

    public void setOperands(O operands) {
        this.operands = operands;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<ProgramNode> getParameters() {
        return parameters;
    }

    public void setParameters(List<ProgramNode> parameters) {
        this.parameters = parameters;
    }
}
