package com.hileco.cortex.decompiler.passes

import com.hileco.cortex.analysis.to
import com.hileco.cortex.decompiler.nodes.DestinationNode
import com.hileco.cortex.decompiler.nodes.IfThenNode
import com.hileco.cortex.decompiler.nodes.InstructionsNode
import com.hileco.cortex.decompiler.nodes.TreeNode
import com.hileco.cortex.instructions.ProgramException.Reason.WINNER
import com.hileco.cortex.instructions.debug.HALT
import com.hileco.cortex.instructions.jumps.EXIT
import com.hileco.cortex.instructions.jumps.JUMP_IF
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class InferIfThenTest {
    @Ignore
    @Test
    fun infer() {
        val sourceModel = listOf<TreeNode>(
                DestinationNode(1, listOf(
                        InstructionsNode(listOf(
                                2 to PUSH(1),
                                3 to PUSH(6),
                                4 to JUMP_IF(),
                                5 to HALT(WINNER)
                        )),
                        DestinationNode(6, listOf(
                                InstructionsNode(listOf(
                                        7 to EXIT()
                                ))
                        ))
                ))
        )
        val targetModel = listOf<TreeNode>(
                DestinationNode(1, listOf(
                        IfThenNode(condition = listOf(InstructionsNode(listOf(
                                2 to PUSH(1)
                        ))), thenBlock = listOf(InstructionsNode(listOf(
                                5 to HALT(WINNER)
                        )))),
                        DestinationNode(6, listOf(
                                InstructionsNode(listOf(
                                        7 to EXIT()
                                ))
                        ))
                ))
        )
        InferIfThen().infer(sourceModel)
        Assert.assertEquals(targetModel.joinToString(separator = "\n"), sourceModel.joinToString(separator = "\n"))
    }
}