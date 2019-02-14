package com.hileco.cortex.analysis.decompile

import java.math.BigInteger

sealed class ModelNode {
    open fun contains(predicate: (ModelNode) -> Boolean): Boolean {
        return false
    }
}

class Model(val nodes: MutableList<ModelNode>) : ModelNode() {
    override fun contains(predicate: (ModelNode) -> Boolean): Boolean {
        return nodes.any { predicate(it) }
    }
}

class Line(val line: Int) : ModelNode()

class Branches(val condition: MutableList<ModelNode>,
               val mappedNodes: Map<BigInteger?, MutableList<ModelNode>>) : ModelNode() {
    override fun contains(predicate: (ModelNode) -> Boolean): Boolean {
        return mappedNodes.values.any { branch -> branch.any { predicate(it) } }
                || condition.any { predicate(it) }
    }
}

class Loop(val nodes: MutableList<ModelNode>) : ModelNode() {
    override fun contains(predicate: (ModelNode) -> Boolean): Boolean {
        return nodes.any { predicate(it) }
    }
}

class Break(val line: Int) : ModelNode()

class Continue(val line: Int) : ModelNode()

class FunctionDefinition(val nodes: MutableList<ModelNode>) : ModelNode() {
    override fun contains(predicate: (ModelNode) -> Boolean): Boolean {
        return nodes.any { predicate(it) }
    }
}

class FunctionCall(val nodes: MutableList<ModelNode>,
                   val functionDefinition: FunctionDefinition) : ModelNode() {
    override fun contains(predicate: (ModelNode) -> Boolean): Boolean {
        return nodes.any { predicate(it) } || functionDefinition.contains(predicate)
    }
}

class FunctionReturn : ModelNode()
