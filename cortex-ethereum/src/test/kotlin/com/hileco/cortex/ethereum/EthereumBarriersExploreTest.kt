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

    private fun barrierExplore(ethereumBarrier: EthereumBarrier) {
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

    @Test
    fun testBarrierExplore000() {
        barrierExplore(ethereumBarriers.byId("000"))
    }

    @Test
    fun testBarrierExplore001() {
        barrierExplore(ethereumBarriers.byId("001"))
    }

    @Test
    fun testBarrierExplore002() {
        barrierExplore(ethereumBarriers.byId("002"))
    }

    @Test
    fun testBarrierExplore003() {
        barrierExplore(ethereumBarriers.byId("003"))
    }

    @Test
    fun testBarrierExplore004() {
        barrierExplore(ethereumBarriers.byId("004"))
    }

    @Test
    fun testBarrierExplore005() {
        barrierExplore(ethereumBarriers.byId("005"))
    }

    @Test
    fun testBarrierExplore006() {
        barrierExplore(ethereumBarriers.byId("006"))
    }

    @Test
    fun testBarrierExplore007() {
        barrierExplore(ethereumBarriers.byId("007"))
    }

    @Test
    fun testBarrierExplore008() {
        barrierExplore(ethereumBarriers.byId("008"))
    }

    @Test
    fun testBarrierExplore009() {
        barrierExplore(ethereumBarriers.byId("009"))
    }

    @Test
    fun testBarrierExplore010() {
        barrierExplore(ethereumBarriers.byId("010"))
    }

    @Test
    fun testBarrierExplore011() {
        barrierExplore(ethereumBarriers.byId("011"))
    }

    @Test
    fun testBarrierExplore012() {
        barrierExplore(ethereumBarriers.byId("012"))
    }

    @Test
    fun testBarrierExplore013() {
        barrierExplore(ethereumBarriers.byId("013"))
    }

    @Test
    fun testBarrierExplore014() {
        barrierExplore(ethereumBarriers.byId("014"))
    }

    @Test
    fun testBarrierExplore015() {
        barrierExplore(ethereumBarriers.byId("015"))
    }

    @Test
    fun testBarrierExplore016() {
        barrierExplore(ethereumBarriers.byId("016"))
    }
}