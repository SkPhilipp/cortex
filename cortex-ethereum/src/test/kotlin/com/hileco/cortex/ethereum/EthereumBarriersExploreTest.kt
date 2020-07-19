package com.hileco.cortex.ethereum

import com.hileco.cortex.documentation.Documentation
import com.hileco.cortex.symbolic.explore.SymbolicProgramExplorer
import com.hileco.cortex.symbolic.explore.strategies.PathTreeExploreStrategy
import com.hileco.cortex.symbolic.vm.SymbolicProgram
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import org.junit.Test

class EthereumBarriersExploreTest {

    private val ethereumBarriers = EthereumBarriers()

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
                    .headingParagraph("Barrier ${ethereumBarrier.id} Symbolic Explore")
                    .source(solution)
        }
    }
}