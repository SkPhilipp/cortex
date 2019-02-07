package com.hileco.cortex.decompiler.passes

import com.hileco.cortex.decompiler.nodes.TreeNode

class InferFunction : InferencePass {
    override fun infer(nodes: List<TreeNode>) {
        //#### Infer Functions
        //
        //Source Model:
        //
        //    JUMP_DESTINATION "LABEL_A" {
        //        ...
        //        JUMP "<unknown value>"
        //    }
        //
        //Target Model:
        //
        //    // embedded in this model on FUNCTION would be the calling convention
        //    FUNCTION "LABEL_A" {
        //        ...
        //    }
    }
}