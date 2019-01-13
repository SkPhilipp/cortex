package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.edges.EdgeMapping
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import java.util.*

class Graph() {
    val graphBlocks: MutableList<GraphBlock> = ArrayList()
    val edgeMapping: EdgeMapping = EdgeMapping()

    constructor(instructions: List<Instruction>) : this() {
        val block = ArrayList<Instruction>()
        var currentBlockLine = 0
        for (currentLine in 0 until instructions.size) {
            val instruction = instructions[currentLine]
            if (instruction is JUMP_DESTINATION) {
                if (!block.isEmpty()) {
                    includeAsBlock(currentBlockLine, block)
                    currentBlockLine = currentLine
                }
                block.clear()
            }
            block.add(instruction)
        }
        if (!block.isEmpty()) {
            includeAsBlock(currentBlockLine, block)
        }
    }

    private fun includeAsBlock(line: Int, instructions: List<Instruction>) {
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

    fun blockAt(line: Int): GraphBlock? {
        return graphBlocks.firstOrNull { graphBlock: GraphBlock ->
            graphBlock.graphNodes.any { graphNode: GraphNode ->
                graphNode.line == line
            }
        }
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
        return graphBlocks.asSequence()
                .flatMap { it.graphNodes.asSequence() }
                .map { it.instruction }
                .toList()
    }

    override fun toString(): String {
        return graphBlocks.joinToString { "$it\n" }
    }
}
