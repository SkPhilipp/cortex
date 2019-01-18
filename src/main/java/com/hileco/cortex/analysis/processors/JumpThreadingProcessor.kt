package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.analysis.GraphBlock
import com.hileco.cortex.analysis.forEachTwo
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.debug.NOOP
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.jumps.JUMP_DESTINATION
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.stack.PUSH

class JumpThreadingProcessor : Processor {
    override fun process(graph: Graph) {
        var replacedAny = false
        graph.graphBlocks.forEach { graphBlock: GraphBlock ->
            graphBlock.graphNodes.forEachTwo { graphNodePush, graphNodeJump ->
                if (graphNodePush.instruction is PUSH && graphNodeJump.instruction is JUMP) {
                    val push = graphNodePush.instruction as PUSH
                    val targetAddres = push.value
                    val targetBlock = graph.blockAt(targetAddres.toInt())
                    if (targetBlock != null && targetBlock.graphNodes.size > 1) {
                        val targetFirst = targetBlock.graphNodes[1].instruction
                        // PUSH X, JUMP ~ JUMP_DESTINATION, EXIT --> PUSH Y, EXIT
                        if (targetFirst is EXIT) {
                            graphNodePush.instruction = NOOP()
                            graphNodeJump.instruction = EXIT()
                            replacedAny = true
                        }
                        // PUSH X, JUMP ~ JUMP_DESTINATION, HALT --> PUSH Y, HALT
                        if (targetFirst is HALT) {
                            graphNodePush.instruction = NOOP()
                            graphNodeJump.instruction = HALT(targetFirst.reason)
                            replacedAny = true
                        }
                        // PUSH X, JUMP ~ JUMP_DESTINATION, PUSH Y , JUMP --> PUSH Y, JUMP ~ ...
                        if (targetFirst is PUSH && targetBlock.graphNodes.size > 2) {
                            val targetSecond = targetBlock.graphNodes[2].instruction
                            if (targetSecond is JUMP && targetFirst.value != push.value) {
                                graphNodePush.instruction = PUSH(targetFirst.value)
                                replacedAny = true
                            }
                        }
                    }
                    // PUSH X, JUMP ~ (final block) JUMP_DESTINATION --> EXIT
                    if (targetBlock != null
                            && targetBlock == graph.graphBlocks.last()
                            && targetBlock.graphNodes.all { it.instruction::class in setOf(EXIT::class, NOOP::class, JUMP_DESTINATION::class) }) {
                        graphNodePush.instruction = NOOP()
                        graphNodeJump.instruction = EXIT()
                        replacedAny = true
                    }
                }
                // PUSH X, JUMP_IF ~ JUMP_DESTINATION, PUSH Y, JUMP --> PUSH Y, JUMP_IF
                if (graphNodePush.instruction is PUSH && graphNodeJump.instruction is JUMP_IF) {
                    val push = graphNodePush.instruction as PUSH
                    val targetAddres = push.value
                    val targetBlock = graph.blockAt(targetAddres.toInt())
                    if (targetBlock != null && targetBlock.graphNodes.size > 2) {
                        val targetFirst = targetBlock.graphNodes[1].instruction
                        val targetSecond = targetBlock.graphNodes[2].instruction
                        if (targetFirst is PUSH && targetSecond is JUMP) {
                            graphNodePush.instruction = PUSH(targetFirst.value)
                            replacedAny = true
                        }
                    }
                }
            }
        }
        if (replacedAny) {
            process(graph)
        }
    }
}
