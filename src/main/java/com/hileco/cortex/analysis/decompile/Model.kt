package com.hileco.cortex.analysis.decompile

import java.math.BigInteger
import java.util.function.Predicate

abstract class ModelNode {
    open fun contains(predicate: Predicate<ModelNode>): Boolean {
        return false
    }
}

class Model(val nodes: MutableList<ModelNode>) : ModelNode() {
    override fun contains(predicate: Predicate<ModelNode>): Boolean {
        return nodes.any { predicate.test(it) }
    }
}

class Line(val line: Int) : ModelNode()

class Branches(val condition: MutableList<ModelNode>,
               val mappedNodes: Map<BigInteger?, MutableList<ModelNode>>) : ModelNode() {
    override fun contains(predicate: Predicate<ModelNode>): Boolean {
        return mappedNodes.values.any { branch -> branch.any { predicate.test(it) } }
                || condition.any { predicate.test(it) }
    }
}

class Loop(val nodes: MutableList<ModelNode>) : ModelNode() {
    override fun contains(predicate: Predicate<ModelNode>): Boolean {
        return nodes.any { predicate.test(it) }
    }
}

class Break : ModelNode()

class Continue : ModelNode()

class FunctionDefinition(val nodes: MutableList<ModelNode>) : ModelNode() {
    override fun contains(predicate: Predicate<ModelNode>): Boolean {
        return nodes.any { predicate.test(it) }
    }
}

class FunctionCall(val nodes: MutableList<ModelNode>,
                   val functionDefinition: FunctionDefinition) : ModelNode() {
    override fun contains(predicate: Predicate<ModelNode>): Boolean {
        return nodes.any { predicate.test(it) } || functionDefinition.contains(predicate)
    }
}

class FunctionReturn : ModelNode()
