package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.edges.Edge
import com.hileco.cortex.analysis.edges.EdgeParameters
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.stack.SWAP
import com.hileco.cortex.vm.ProgramZone
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class GraphNode(val instruction: AtomicReference<Instruction>,
                val line: Int,
                val edges: ArrayList<Edge> = ArrayList()) {
    // TODO: And ensure that all child parameter do not have multiple parameter-consumers
    fun isSelfContained(): Boolean {
        return fully { graphNode -> SELF_CONTAINED_ZONES.containsAll(graphNode.instruction.get().instructionModifiers) && graphNode.instruction.get() !is SWAP }
    }

    private fun addInstructionsByLine(list: MutableList<Pair<Int, AtomicReference<Instruction>>>) {
        list.add(line to instruction)
        edges.asSequence()
                .filterIsInstance<EdgeParameters>()
                .flatMap { it.graphNodes.asSequence() }
                .forEach { it?.addInstructionsByLine(list) }
    }

    fun parameters(): List<GraphNode?> {
        return edges.asSequence()
                .filterIsInstance<EdgeParameters>()
                .flatMap { it.graphNodes.asSequence() }
                .toList()
    }

    fun toInstructions(): List<Instruction> {
        val list = ArrayList<Pair<Int, AtomicReference<Instruction>>>()
        addInstructionsByLine(list)
        return list.asSequence()
                .sortedBy { it.first }
                .map { it.second.get() }
                .toList()
    }

    private fun allParameters(predicate: (GraphNode) -> Boolean): Boolean {
        return edges.asSequence()
                .filterIsInstance<EdgeParameters>()
                .flatMap { it.graphNodes.asSequence() }
                .all { it != null && predicate(it) }
    }

    fun hasOneParameter(index: Int, predicate: (GraphNode) -> Boolean): Boolean {
        val nodes = edges.asSequence()
                .filterIsInstance<EdgeParameters>()
                .map { it.graphNodes[index] }
                .filterNotNull()
                .toList()
        return nodes.size == 1 && nodes.all(predicate)
    }

    fun isInstruction(classes: Collection<Class<*>>): Boolean {
        return instruction.get()::class.java in classes
    }

    fun isInstruction(vararg classes: Class<*>): Boolean {
        return instruction.get()::class.java in classes
    }

    private fun fully(predicate: (GraphNode) -> Boolean): Boolean {
        return predicate(this) && allParameters { graphNode -> graphNode.fully(predicate) }
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        this.format(this, stringBuilder, 0)
        return "$stringBuilder"
    }

    private fun format(graphNode: GraphNode?, stringBuilder: StringBuilder, offset: Int) {
        if (graphNode != null) {
            stringBuilder.append(String.format("[%03d]", graphNode.line))
        } else {
            stringBuilder.append("     ")
        }
        for (i in 0 until offset) {
            stringBuilder.append(' ')
        }
        stringBuilder.append(' ')
        if (graphNode != null) {
            stringBuilder.append(graphNode.instruction.toString().trim { it <= ' ' })
            for (parameter in graphNode.parameters()) {
                stringBuilder.append('\n')
                this.format(parameter, stringBuilder, offset + 2)
            }
        } else {
            stringBuilder.append("?")
        }
    }

    companion object {
        private val SELF_CONTAINED_ZONES = HashSet(setOf(ProgramZone.STACK))
    }
}
