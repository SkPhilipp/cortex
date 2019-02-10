package com.hileco.cortex.decompiler.passes

import com.hileco.cortex.analysis.to
import com.hileco.cortex.decompiler.nodes.*
import com.hileco.cortex.instructions.jumps.JUMP
import com.hileco.cortex.instructions.stack.PUSH
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

class InferContinueTest {
    @Ignore
    @Test
    fun infer() {
        val sourceModel = listOf<TreeNode>(
                DestinationNode(1, listOf(
                        DoWhileNode(doLine = 2, doBlock = listOf(
                                InstructionsNode(listOf(
                                        3 to PUSH(5),
                                        4 to JUMP()
                                ))
                        ), conditionLine = 5, condition = listOf(
                                InstructionsNode(listOf(
                                        6 to PUSH(1)
                                ))
                        )),
                        DestinationNode(6, listOf())
                ))
        )
        val targetModel = listOf<TreeNode>(
                DestinationNode(1, listOf(
                        DoWhileNode(doLine = 2, doBlock = listOf(
                                ContinueNode()
                        ), conditionLine = 5, condition = listOf(
                                InstructionsNode(listOf(
                                        6 to PUSH(1)
                                ))
                        )),
                        DestinationNode(6, listOf())
                ))
        )
        InferContinue().infer(sourceModel)
        Assert.assertEquals(targetModel.joinToString(separator = "\n"), sourceModel.joinToString(separator = "\n"))
    }
}