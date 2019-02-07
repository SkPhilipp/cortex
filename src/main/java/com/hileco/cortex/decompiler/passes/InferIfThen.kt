package com.hileco.cortex.decompiler.passes

import com.hileco.cortex.decompiler.nodes.TreeNode

class InferIfThen : InferencePass {
    override fun infer(nodes: List<TreeNode>) {
        //#### Infer IF
        //
        //Source Model:
        //
        //    JUMP_DESTINATION "LABEL_A" {
        //        ... A
        //        JUMP_IF "LABEL_B"
        //        ... B
        //        JUMP_DESTINATION "LABEL_B" {
        //            ... C
        //        }
        //    }
        //
        //Target Model:
        //
        //    JUMP_DESTINATION "LABEL_A" {
        //        IF {
        //            ... A
        //            IS_ZERO
        //        } THEN {
        //            ... B
        //        }
        //        ... C
        //    }
        //
        //Initial rule:
        //- within the same block, some instructions may be skipped by JUMP\_IF
    }
}