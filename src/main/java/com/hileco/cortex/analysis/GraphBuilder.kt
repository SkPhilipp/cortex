package com.hileco.cortex.analysis

import com.hileco.cortex.analysis.processors.Processor
import com.hileco.cortex.instructions.Instruction
import lombok.Value

class GraphBuilder(val processors: List<Processor>) {

    fun build(instructions: List<Instruction>): Graph {
        val graph = Graph(instructions)
        processors.forEach { processor -> processor.process(graph) }
        return graph
    }
}
