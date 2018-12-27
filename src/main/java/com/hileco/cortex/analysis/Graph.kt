package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.edges.Edge
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.stream.Collectors

class Graph() {
    internal val graphBlocks: MutableList<GraphBlock>
    val edges: MutableList<Edge>

    init {
        graphBlocks = ArrayList()
        edges = ArrayList()
    }

    constructor(instructions: List<Instruction>) : this() {
        val block = ArrayList<AtomicReference<Instruction>>()
        var currentBlockLine = 0
        for (currentLine in 0 until instructions.size) {
            val instructionReference = AtomicReference(instructions[currentLine])
            if (instructionReference.get() is JUMP_DESTINATION) {
                if (!block.isEmpty()) {
                    includeAsBlock(currentBlockLine, block)
                    currentBlockLine = currentLine
                }
                block.clear()
            }
            block.add(instructionReference)
        }
        if (!block.isEmpty()) {
            includeAsBlock(currentBlockLine, block)
        }
    }

    private fun includeAsBlock(line: Int, instructions: List<AtomicReference<Instruction>>) {
        val block = GraphBlock()
        block.include(line, instructions)
        graphBlocks.add(block)
    }

    private fun indexOf(graphBlock: GraphBlock): Int {
        val index = graphBlocks.indexOf(graphBlock)
        if (index == -1) {
            throw IllegalArgumentException()
        }
        return index
    }

    fun mergeUpwards(graphBlock: GraphBlock) {
        val index = this.indexOf(graphBlock)
        if (index == 0) {
            return
        }
        val target = graphBlocks[index - 1]
        target.append(graphBlock)
        graphBlocks.remove(graphBlock)
    }

    fun mergeDownwards(graphBlock: GraphBlock) {
        val index = this.indexOf(graphBlock)
        if (index + 1 >= graphBlocks.size) {
            return
        }
        val target = graphBlocks[index + 1]
        graphBlock.append(target)
        graphBlocks.remove(target)
    }

    fun remove(graphBlock: GraphBlock) {
        graphBlocks.remove(graphBlock)
    }

    fun replace(original: GraphBlock, replacement: GraphBlock) {
        val index = this.indexOf(original)
        graphBlocks[index] = replacement
    }

    fun toInstructions(): List<Instruction> {
        return graphBlocks.stream()
                .flatMap { graphBlock -> graphBlock.graphNodes.stream() }
                .map { graphNode -> graphNode.instruction.get() }
                .collect(Collectors.toList())
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (graphBlock in graphBlocks) {
            stringBuilder.append(graphBlock)
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }
}
