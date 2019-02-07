package com.hileco.cortex.decompiler.passes

import com.hileco.cortex.decompiler.nodes.TreeNode

class InferContinue : InferencePass {
    override fun infer(nodes: List<TreeNode>) {
        //#### Infer CONTINUE & BREAK
        //
        //Source Model:
        //
        //    JUMP_DESTINATION "LABEL_A" {
        //        DO { "LABEL_DO"
        //            ... WHILE body
        //            JUMP "WHILE"
        //            ...
        //            JUMP "LABEL_B"
        //            ...
        //        } "LABEL_WHILE" WHILE ( ... WHILE condition )
        //        JUMP_DESTINATION "LABEL_B" {
        //            ...
        //        }
        //    }
        //
        //Target Model:
        //
        //    JUMP_DESTINATION "LABEL_A" {
        //        DO { "LABEL_DO" "LOOP_1"
        //            ... WHILE body
        //            CONTINUE "LOOP_1"
        //            ...
        //            BREAK "LOOP_1"
        //            ...
        //        } "LABEL_WHILE" WHILE ( ... WHILE condition )
        //        JUMP_DESTINATION "LABEL_B" {
        //            ...
        //        }
        //    }
    }
}