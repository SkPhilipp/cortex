package com.hileco.cortex.decompiler.passes

import com.hileco.cortex.decompiler.nodes.TreeNode

class InferFunctionCall : InferencePass {
    override fun infer(nodes: List<TreeNode>) {
        // #### Infer Function Calls
        //
        //Source Model:
        //
        //    JUMP_DESTINATION "LABEL_A" {
        //        ...
        //        ... // calling convention setup for FUNCTION "LABEL_B" to return to "LABEL_C"
        //        JUMP "LABEL_B"
        //        JUMP_DESTINATION "LABEL_C" {
        //            ...
        //        }
        //    }
        //
        //    FUNCTION "LABEL_B" {
        //        ...
        //    }
        //
        //Target Model:
        //
        //    JUMP_DESTINATION "LABEL_A" {
        //        ...
        //        FUNCTION_CALL "LABEL_B"
        //        ...
        //    }
        //
        //    FUNCTION "LABEL_B" {
        //        ...
        //    }
        //
        //#### Function Calls Note
        //
        //Function calling conventions should be verified to not be exploitable. Obfuscated or otherwise exteptionally complex calling conventions are
        //more likely to allow for flow control which may not be intended by the developer. Such calling conventions should not be overlooked.
    }
}