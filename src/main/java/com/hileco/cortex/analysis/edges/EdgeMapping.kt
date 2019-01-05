package com.hileco.cortex.analysis.edges

import com.hileco.cortex.analysis.GraphBlock
import com.hileco.cortex.analysis.GraphNode
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.stack.SWAP
import com.hileco.cortex.vm.ProgramZone

class EdgeMapping {
    private val mapping: MutableMap<Class<*>, MutableMap<Any, MutableList<Edge>>> = HashMap()

    fun add(key: GraphNode, element: Edge) {
        mapFor(element::class.java, key).add(element)
    }

    fun add(key: GraphBlock, element: Edge) {
        mapFor(element::class.java, key).add(element)
    }

    fun add(element: Edge) {
        mapFor(element::class.java, this).add(element)
    }

    fun <T : Edge> remove(key: GraphNode, type: Class<T>, predicate: (T) -> Boolean = { true }) {
        mapFor(type, key).removeAll { sequenceOf(it).filterIsInstance(type).all(predicate) }
    }

    fun <T : Edge> remove(key: GraphBlock, type: Class<T>, predicate: (T) -> Boolean = { true }) {
        mapFor(type, key).removeAll { sequenceOf(it).filterIsInstance(type).all(predicate) }
    }

    fun <T : Edge> remove(type: Class<T>, predicate: (T) -> Boolean = { true }) {
        mapFor(type, this).removeAll { sequenceOf(it).filterIsInstance(type).all(predicate) }
    }

    fun <T : Edge> removeAll(type: Class<T>, predicate: (T) -> Boolean = { true }) {
        mapping[type]?.forEach { entry -> entry.value.removeAll { type.isInstance(it) && predicate(it as T) } }
    }

    fun <T : Edge> get(key: GraphNode, type: Class<T>): Sequence<T> {
        return mapFor(type, key).asSequence().filterIsInstance(type)
    }

    fun <T : Edge> get(key: GraphBlock, type: Class<T>): Sequence<T> {
        return mapFor(type, key).asSequence().filterIsInstance(type)
    }

    fun <T : Edge> get(type: Class<T>): Sequence<T> {
        return mapFor(type, this).asSequence().filterIsInstance(type)
    }

    private fun <T> mapFor(type: Class<T>, key: Any): MutableList<Edge> {
        return mapping.getOrPut(type, defaultValue = { HashMap() })
                .getOrPut(key, defaultValue = { ArrayList() })
    }

    fun fully(graphNode: GraphNode, predicate: (GraphNode) -> Boolean): Boolean {
        return predicate(graphNode) && allParameters(graphNode) { parameter -> fully(parameter, predicate) }
    }

    // TODO: And ensure that all child parameter do not have multiple parameter-consumers
    fun isSelfContained(graphNode: GraphNode): Boolean {
        return fully(graphNode) {
            setOf(ProgramZone.STACK).containsAll(it.instruction.instructionModifiers) && it.instruction !is SWAP
        }
    }

    private fun addInstructionsByLine(graphNode: GraphNode, list: MutableList<GraphNode>) {
        list.add(graphNode)
        get(graphNode, EdgeParameters::class.java)
                .flatMap { it.graphNodes.asSequence() }
                .filterNotNull()
                .forEach { addInstructionsByLine(it, list) }
    }

    fun parameters(graphNode: GraphNode): Sequence<GraphNode?> {
        return get(graphNode, EdgeParameters::class.java)
                .flatMap { it.graphNodes.asSequence() }
    }

    fun toInstructions(graphNode: GraphNode): List<Instruction> {
        val list = ArrayList<GraphNode>()
        addInstructionsByLine(graphNode, list)
        return list.asSequence()
                .sortedBy { it.line }
                .map { it.instruction }
                .toList()
    }

    private fun allParameters(graphNode: GraphNode, predicate: (GraphNode) -> Boolean): Boolean {
        return get(graphNode, EdgeParameters::class.java)
                .flatMap { it.graphNodes.asSequence() }
                .all { it != null && predicate(it) }
    }

    fun hasOneParameter(graphNode: GraphNode, index: Int, predicate: (GraphNode) -> Boolean): Boolean {
        val node = get(graphNode, EdgeParameters::class.java)
                .map { it.graphNodes[index] }
                .filterNotNull()
                .singleOrNull()
        return node != null && predicate(node)
    }
}
