package com.hileco.cortex.ethereum

import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.attack.Attacker
import com.hileco.cortex.documentation.Documentation
import org.junit.Ignore
import org.junit.Test

class EthereumOperationAttackTest {

    private val ethereumBarriers = EthereumBarriers()

    // TODO: Resolve & un-ignore transpiled barrier attack
    @Ignore("Multi-width instructions on basic graph appear to have triggered something in Attacker")
    @Test
    fun barriersAttack() {
        ethereumBarriers.all().forEach { ethereumBarrier ->
            val attacker = Attacker(Attacker.TARGET_IS_CALL, listOf(Attacker.CONSTRAINT_CALL_ADDRESS(1234)))
            val graph = GraphBuilder.BASIC_GRAPH_BUILDER.build(ethereumBarrier.cortexInstructions)
            try {
                val solutions = attacker.solve(graph)
                Documentation.of(EthereumBarriers::class.java.simpleName)
                        .headingParagraph("Barrier ${ethereumBarrier.id} Attacker Solve")
                        .source(solutions)
            } catch (e: UnsupportedOperationException) {
                Documentation.of(EthereumBarriers::class.java.simpleName)
                        .headingParagraph("Ethereum Barrier ${ethereumBarrier.id} Attacker Solve")
                        .paragraph("Program contains an Attacker Solver unsupported operation")
            }
        }
    }
}