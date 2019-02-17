package com.hileco.cortex.analysis.attack

import com.hileco.cortex.analysis.attack.PathGenerator.RotationResult.*
import com.hileco.cortex.analysis.edges.Flow
import com.hileco.cortex.analysis.edges.FlowMapping
import com.hileco.cortex.analysis.edges.FlowType.*

class PathGenerator(private val flowMapping: FlowMapping,
                    private val repeatLimit: Int = 500) {
    private val path: MutableList<Flow> = arrayListOf()

    init {
        var expanded: Boolean
        do {
            expanded = expand()
        } while (expanded)
    }

    /**
     * Retrieves all possible next [Flow]s to navigate to starting from [current].
     */
    private fun optionsFrom(current: Flow): List<Flow> {
        val wrappingFlow = flowMapping.flows.asSequence()
                .filter { it.type == PROGRAM_FLOW }
                .filter { it.target != null && it.target >= current.target!! }
                .filter { it.source <= current.target!! }
                .maxBy { it.target!! } ?: return listOf()
        return flowMapping.flows.asSequence()
                .filter { next -> (next.type == PROGRAM_FLOW) == (current.type != PROGRAM_FLOW) }
                .filter { next ->
                    return@filter when (next.type) {
                        PROGRAM_FLOW -> current.target!! in next.source..(next.target!! - 1)
                        PROGRAM_END -> wrappingFlow.target!! == next.source
                        INSTRUCTION_JUMP_IF -> next.source in wrappingFlow.source..current.target!!
                        INSTRUCTION_JUMP -> next.source in wrappingFlow.source..current.target!!
                        INSTRUCTION_JUMP_IF_DYNAMIC -> false
                        INSTRUCTION_JUMP_DYNAMIC -> false
                    }
                }
                .filter { next ->
                    return@filter path.count { it == next } < repeatLimit
                }
                .toList()
    }

    /**
     * Retries the initial [Flow] representing the start of the program.
     */
    private fun initialOption(): Flow? {
        return flowMapping.flows.asSequence()
                .filter { it.type == PROGRAM_FLOW }
                .filter { it.source == 0 }
                .maxBy { it.target!! }
    }

    enum class RotationResult {
        PATH_IS_EMPTY,
        OPTIONS_EXHAUSTED,
        OK
    }

    /**
     * Guarantees to remove the last entry in the path, and attempts to add the next option.
     */
    private fun rotate(): RotationResult {
        if (path.isEmpty()) {
            return PATH_IS_EMPTY
        }
        val previousEntry = path.removeAt(path.size - 1)
        val previousEntryParent = path.lastOrNull() ?: return PATH_IS_EMPTY
        val options = optionsFrom(previousEntryParent)
        val previousEntryIndex = options.indexOf(previousEntry)
        if (previousEntryIndex + 1 >= options.size) {
            return OPTIONS_EXHAUSTED
        }
        val nextEntry = options[previousEntryIndex + 1]
        path.add(nextEntry)
        return OK
    }

    /**
     * Adds to the path by adding the first possible option.
     *
     * @return true when an option was added, false when not possible
     */
    private fun expand(): Boolean {
        val currentLastEntry = path.lastOrNull()
        when {
            currentLastEntry == null -> {
                val option = initialOption() ?: return false
                path.add(option)
                return true
            }
            currentLastEntry.target == null -> {
                return false
            }
            else -> {
                val option = optionsFrom(currentLastEntry).firstOrNull() ?: return false
                path.add(option)
                return true
            }
        }
    }

    /**
     * Rotate to the next path, if any, and expand from there.
     */
    private fun next() {
        var rotateResult: RotationResult
        do {
            rotateResult = rotate()
        } while (rotateResult == OPTIONS_EXHAUSTED)
        if (rotateResult != PATH_IS_EMPTY) {
            var expanded: Boolean
            do {
                expanded = expand()
            } while (expanded)
        }
    }

    fun asSequence() = sequence {
        while (path.isNotEmpty()) {
            yield(path.asSequence().toList())
            next()
        }
    }
}
