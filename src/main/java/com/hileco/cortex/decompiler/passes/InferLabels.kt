package com.hileco.cortex.decompiler.passes

import com.hileco.cortex.decompiler.nodes.TreeNode

class InferLabels : InferencePass {
    override fun infer(nodes: List<TreeNode>) {
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
    }
}