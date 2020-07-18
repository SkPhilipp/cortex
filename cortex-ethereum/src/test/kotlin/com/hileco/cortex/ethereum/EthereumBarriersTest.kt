package com.hileco.cortex.ethereum

import com.hileco.cortex.analysis.GraphBuilder.Companion.BASIC_GRAPH_BUILDER
import com.hileco.cortex.analysis.VisualGraph
import com.hileco.cortex.analysis.attack.Attacker
import com.hileco.cortex.analysis.attack.Attacker.Companion.CONSTRAINT_CALL_ADDRESS
import com.hileco.cortex.analysis.attack.Attacker.Companion.TARGET_IS_CALL
import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.symbolic.explore.SymbolicProgramExplorer
import com.hileco.cortex.symbolic.explore.strategies.PathTreeExploreStrategy
import com.hileco.cortex.symbolic.vm.SymbolicProgram
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import org.junit.Ignore
import org.junit.Test

class EthereumBarriersTest {

    private val ethereumBarriers = EthereumBarriers()

    @Test
    fun barriersVisualBasic() {
        ethereumBarriers.all().forEach { ethereumBarrier ->
            val graph = BASIC_GRAPH_BUILDER.build(ethereumBarrier.cortexInstructions)
            val visualGraph = VisualGraph()
            visualGraph.map(graph)
            Documentation.of(EthereumBarriers::class.java.simpleName)
                    .headingParagraph("Ethereum Barrier ${ethereumBarrier.id} Visualized (Basic)")
                    .image(visualGraph::render)
        }
    }

    @Test
    fun barriersBytecode() {
        ethereumBarriers.all().forEach { ethereumBarrier ->
            Documentation.of(EthereumBarriers::class.java.simpleName)
                    .headingParagraph("Ethereum Barrier ${ethereumBarrier.id} Bytecode")
                    .source(ethereumBarrier.contractCode)
        }
    }

    @Test
    fun barriersCortexInstructions() {
        ethereumBarriers.all().forEach { ethereumBarrier ->
            Documentation.of(EthereumBarriers::class.java.simpleName)
                    .headingParagraph("Ethereum Barrier ${ethereumBarrier.id} Cortex Instructions")
                    .source(ethereumBarrier.cortexInstructions)
        }
    }

    @Test
    fun barriersEthereumInstructions() {
        ethereumBarriers.all().forEach { ethereumBarrier ->
            Documentation.of(EthereumBarriers::class.java.simpleName)
                    .headingParagraph("Ethereum Barrier ${ethereumBarrier.id} Ethereum Instructions")
                    .source(ethereumBarrier.ethereumInstructions)
        }
    }

    // TODO: Reslve & unignore transpiled barrier attack
    @Ignore("Multi-width instructions on basic graph appear to have triggered something in Attacker")
    @Test
    fun barriersAttack() {
        ethereumBarriers.all().forEach { ethereumBarrier ->
            val attacker = Attacker(TARGET_IS_CALL, listOf(CONSTRAINT_CALL_ADDRESS(1234)))
            val graph = BASIC_GRAPH_BUILDER.build(ethereumBarrier.cortexInstructions)
            try {
                val solutions = attacker.solve(graph)
                Documentation.of(EthereumBarriers::class.java.simpleName)
                        .headingParagraph("Ethereum Barrier ${ethereumBarrier.id} Attacker Solve")
                        .source(solutions)
            } catch (e: UnsupportedOperationException) {
                Documentation.of(EthereumBarriers::class.java.simpleName)
                        .headingParagraph("Ethereum Barrier ${ethereumBarrier.id} Attacker Solve")
                        .paragraph("Program contains an Attacker Solver unsupported operation")
            }
        }
    }

    @Test
    fun barriersExplore() {
        ethereumBarriers.all().forEach { ethereumBarrier ->
            val program = SymbolicProgram(ethereumBarrier.cortexInstructions)
            val programContext = SymbolicProgramContext(program)
            val virtualMachine = SymbolicVirtualMachine(programContext)
            val strategy = PathTreeExploreStrategy()
            val symbolicProgramExplorer = SymbolicProgramExplorer(strategy)
            symbolicProgramExplorer.explore(virtualMachine)
            val solution = strategy.solve()
            Documentation.of(EthereumBarriers::class.java.simpleName)
                    .headingParagraph("Ethereum Barrier ${ethereumBarrier.id} Symbolic Program Explore")
                    .source(solution)
        }
    }
}