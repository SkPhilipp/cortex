package com.hileco.cortex.analysis.decompile

import com.hileco.cortex.analysis.Graph

class Decompiler {
    fun decompile(graph: Graph): Model {
        val model = Model(arrayListOf())
        listOf(
                InferencePassLoops(),
                InferencePassBranches(),
                InferencePassFunctions()
        ).forEach {
            it.infer(model, graph)
        }
        return model
    }
}