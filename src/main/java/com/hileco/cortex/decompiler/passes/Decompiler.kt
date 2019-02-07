package com.hileco.cortex.decompiler.passes

import com.hileco.cortex.decompiler.nodes.TreeNode

class Decompiler(val inferencePasses: List<InferencePass> = listOf(
        InferLabels(),
        InferIfThen(),
        InferElse(),
        InferDoWhile(),
        InferWhile(),
        InferContinue(),
        InferBreak(),
        InferFunction(),
        InferFunctionCall()
)) {
    fun decompile(nodes: List<TreeNode>) {
        //#### Complexity Warning Note
        //
        //Programs which are essentially "decomiled" in this manner and which still remain with any or a high amount of (to be evaluated) manual JUMPing
        //instructions are likely be to obfuscated. The process should warn in case a (valid) program described as such is encountered in the wild.
        //These hypothetical programs are likely obfuscared and might could new rules to decompile them. (Or all rules here apply to every program, who knows...)

        inferencePasses.forEach {
            it.infer(nodes)
        }
    }
}