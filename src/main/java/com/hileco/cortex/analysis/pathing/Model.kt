package com.hileco.cortex.analysis.pathing

import java.math.BigInteger
import java.util.function.Predicate

abstract class ModelNode {
    open fun contains(predicate: Predicate<ModelNode>): Boolean {
        return false
    }
}

class Conditional(val condition: List<ModelNode>,
                  val branches: Map<BigInteger?, List<ModelNode>>) : ModelNode() {
    override fun contains(predicate: Predicate<ModelNode>): Boolean {
        return condition.any { predicate.test(it) } || branches.values.any { branch -> branch.any { predicate.test(it) } }
    }
}

class Function(val nodes: List<ModelNode>) : ModelNode() {
    override fun contains(predicate: Predicate<ModelNode>): Boolean {
        return nodes.any { predicate.test(it) }
    }
}

class FunctionReturn : ModelNode()

class Instructions(val lineFrom: Int, val lineTo: Int) : ModelNode()

class Loop(val nodes: List<ModelNode>) : ModelNode() {
    override fun contains(predicate: Predicate<ModelNode>): Boolean {
        return nodes.any { predicate.test(it) }
    }
}

enum class LoopReturnType {
    BREAK, CONTINUE
}

class LoopReturn(val line: Int, val type: LoopReturnType) : ModelNode()
