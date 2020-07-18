package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.edges.EdgeMapping
import com.hileco.cortex.vm.instructions.Instruction
import com.hileco.cortex.vm.instructions.jumps.JUMP_DESTINATION
import java.util.*

class Graph() {
    val graphBlocks: MutableList<GraphBlock> = ArrayList()
    val edgeMapping: EdgeMapping = EdgeMapping()

    constructor(instructions: List<Instruction>) : this() {
        val block = ArrayList<Instruction>()
        var blockPosition = 0
        var currentPosition = 0
        for (instructionsIndex in instructions.indices) {
            val instruction = instructions[instructionsIndex]
            if (instruction is JUMP_DESTINATION) {
                if (block.isNotEmpty()) {
                    includeAsBlock(blockPosition, block)
                    blockPosition = currentPosition
                }
                block.clear()
            }
            block.add(instruction)
            currentPosition += instruction.width
        }
        if (block.isNotEmpty()) {
            includeAsBlock(blockPosition, block)
        }
    }

    private fun includeAsBlock(position: Int, instructions: List<Instruction>) {
        val block = GraphBlock()
        block.include(position, instructions)
        graphBlocks.add(block)
    }

    private fun indexOf(graphBlock: GraphBlock): Int {
        val index = graphBlocks.indexOf(graphBlock)
        if (index == -1) {
            throw IllegalArgumentException()
        }
        return index
    }

    fun blockAt(position: Int): GraphBlock? {
        return graphBlocks.firstOrNull { graphBlock: GraphBlock ->
            graphBlock.graphNodes.any { graphNode: GraphNode ->
                graphNode.position == position
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
