package com.hileco.cortex.analysis.decompile

import com.hileco.cortex.analysis.Graph

interface InferencePass {
    fun infer(model: Model, graph: Graph)
}