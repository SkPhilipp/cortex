package com.hileco.cortex.decompiler.passes

import com.hileco.cortex.decompiler.nodes.TreeNode

class InferWhile : InferencePass {
    override fun infer(nodes: List<TreeNode>) {
        //#### Infer DO-WHILE
        //
        //Source Model:
        //
        //    JUMP_DESTINATION "LABEL_A" {
        //        ... before WHILE
        //        JUMP "LABEL_WHILE"
        //    }
        //
        //    JUMP_DESTINATION "LABEL_DO" {
        //        ... WHILE body
        //        JUMP_DESTINATION "LABEL_WHILE" {
        //            ... WHILE condition
        //            JUMP_IF "LABEL_DO"
        //			... after WHILE
        //        }
        //    }
        //
        //Possible Intermediate Model:
        //
        //    JUMP_DESTINATION "LABEL_A" {
        //        ... before WHILE
        //        JUMP "LABEL_WHILE"
        //    }
        //
        //    DO { "LABEL_DO"
        //		... WHILE body
        //	} "LABEL_WHILE" WHILE ( ... WHILE condition )
        //
        //Target Model:
        //
        //    JUMP_DESTINATION "LABEL_A" {
        //        ... before WHILE
        //        "LABEL_WHILE" WHILE(... WHILE-condition) { "LABEL_DO"
        //            ... WHILE body
        //        }
        //        ... after WHILE
        //    }
    }
}