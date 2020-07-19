package com.hileco.cortex.ethereum

import com.hileco.cortex.analysis.GraphBuilder.Companion.BASIC_GRAPH_BUILDER
import com.hileco.cortex.analysis.VisualGraph
import com.hileco.cortex.documentation.Documentation
import org.junit.Test

class EthereumBarriersDocumentTest {

    private val ethereumBarriers = EthereumBarriers()

    @Test
    fun barriersVisualBasic() {
        ethereumBarriers.all().forEach { ethereumBarrier ->
            val graph = BASIC_GRAPH_BUILDER.build(ethereumBarrier.cortexInstructions)
            val visualGraph = VisualGraph()
            visualGraph.map(graph)
            Documentation.of(EthereumBarriers::class.java.simpleName)
                    .headingParagraph("Barrier ${ethereumBarrier.id} Visualized")
                    .image(visualGraph::render)
        }
    }

    @Test
    fun barriersBytecode() {
        ethereumBarriers.all().forEach { ethereumBarrier ->
            Documentation.of(EthereumBarriers::class.java.simpleName)
                    .headingParagraph("Barrier ${ethereumBarrier.id} Bytecode")
                    .source(ethereumBarrier.contractCode)
        }
    }

    @Test
    fun barriersCortexInstructions() {
        ethereumBarriers.all().forEach { ethereumBarrier ->
            Documentation.of(EthereumBarriers::class.java.simpleName)
                    .headingParagraph("Barrier ${ethereumBarrier.id} Instructions")
                    .source(ethereumBarrier.cortexInstructions)
        }
    }
}