package com.hileco.cortex.ethereum

import com.hileco.cortex.collections.BackedInteger.Companion.ZERO_32
import com.hileco.cortex.collections.toBackedInteger
import com.hileco.cortex.symbolic.explore.SymbolicProgramExplorer
import com.hileco.cortex.symbolic.explore.strategies.CustomExploreStrategy
import com.hileco.cortex.symbolic.expressions.Expression
import com.hileco.cortex.symbolic.vm.SymbolicProgram
import com.hileco.cortex.symbolic.vm.SymbolicProgramContext
import com.hileco.cortex.symbolic.vm.SymbolicVirtualMachine
import org.junit.Assert
import org.junit.Test

class EthereumBarriersExploreTest {

    private val ethereumBarriers = EthereumBarriers()

    private fun barrierExplore(ethereumBarrier: EthereumBarrier) {
        val program = SymbolicProgram(ethereumBarrier.cortexInstructions)
        val programContext = SymbolicProgramContext(program)
        val virtualMachine = SymbolicVirtualMachine(programContext)
        val strategy = CustomExploreStrategy()
        strategy.withCompleteFilter { symbolicVirtualMachine -> !symbolicVirtualMachine.transfers.isEmpty() }
        strategy.withCondition { symbolicVirtualMachine ->
            val valueTransferConditions = symbolicVirtualMachine.transfers.asSequence()
                    .map { symbolicTransfer ->
                        val targetIsExplorer = Expression.Equals(symbolicTransfer.target, Expression.Value(EXPLORER_ADDRESS))
                        val valueIsPositive = Expression.GreaterThan(symbolicTransfer.value, Expression.Value(ZERO_32))
                        Expression.constructAnd(listOf(targetIsExplorer, valueIsPositive))
                    }
                    .toList()
            Expression.constructOr(valueTransferConditions)
        }
        val symbolicProgramExplorer = SymbolicProgramExplorer(strategy)
        symbolicProgramExplorer.explore(virtualMachine)
        val solution = strategy.solve()
        Assert.assertNotNull(solution.solvable)
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

    companion object {
        val EXPLORER_ADDRESS = "0xdeadd00d".toBackedInteger()
    }
}