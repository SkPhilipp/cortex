package com.hileco.cortex.decompiler.passes

import com.hileco.cortex.analysis.Graph
import com.hileco.cortex.decompiler.nodes.TreeNode

class Decompiler {
    fun decompile(graph: Graph): List<TreeNode> {
        val nodes = listOf<TreeNode>()
        //#### Infer labels
        //
        //A step forward from the current Graph model of splitting by JUMP\_DESTINATION, where blocks can be contained inside other
        //blocks when normal flow control can lead to it. Initially a JUMP\_DESTINATION block can only contain one other block at
        //its tail, however this structure can be used to build more complex statements upon.
        //
        //Model:
        //
        //    JUMP_DESTINATION "LABEL_A" {
        //        ...
        //        JUMP_IF "LABEL_B"
        //        ...
        //        JUMP_DESTINATION "LABEL_B" {
        //            ...
        //        }
        //    }
        //#### Complexity Warning Note
        //
        //Programs which are essentially "decomiled" in this manner and which still remain with any or a high amount of (to be evaluated) manual JUMPing
        //instructions are likely be to obfuscated. The process should warn in case a (valid) program described as such is encountered in the wild.
        //These hypothetical programs are likely obfuscared and might could new rules to decompile them. (Or all rules here apply to every program, who knows...)
        listOf(
                InferIfThen(),
                InferElse(),
                InferDoWhile(),
                InferWhile(),
                InferContinue(),
                InferBreak(),
                InferFunction(),
                InferFunctionCall()
        ).forEach {
            it.infer(nodes)
        }
        return nodes
    }
}