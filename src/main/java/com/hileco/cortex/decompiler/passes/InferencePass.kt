package com.hileco.cortex.decompiler.passes

import com.hileco.cortex.decompiler.nodes.TreeNode

interface InferencePass {
    fun infer(nodes: List<TreeNode>)
}