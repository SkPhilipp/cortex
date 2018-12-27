package com.hileco.cortex.analysis.processors

import com.hileco.cortex.analysis.Graph

interface Processor {
    fun process(graph: Graph)
}
