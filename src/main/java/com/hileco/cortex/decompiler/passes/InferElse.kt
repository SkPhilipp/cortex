package com.hileco.cortex.decompiler.passes

import com.hileco.cortex.decompiler.nodes.TreeNode

class InferElse : InferencePass {
    override fun infer(nodes: List<TreeNode>) {
        //#### Infer ELSE
        //
        //Source Model:
        //
        //    JUMP_DESTINATION "LABEL_A" {
        //        IF {
        //            ... A
        //        } THEN {
        //            ... B
        //			JUMP "LABEL_ENDIF"
        //        }
        //        ... C
        //		JUMP "LABEL_ENDIF"
        //    }
        //
        //	JUMP_DESTINATION "LABEL_ENDIF" {
        //		... D
        //	}
        //
        //Target Model:
        //
        //    JUMP_DESTINATION "LABEL_A" {
        //        IF {
        //            ... A
        //        } THEN {
        //            ... B
        //        } ELSE {
        //            ... C
        //		}
        //	    JUMP "LABEL_ENDIF"
        //    }
        //
        //	JUMP_DESTINATION "LABEL_ENDIF" {
        //		... D
        //	}
    }
}