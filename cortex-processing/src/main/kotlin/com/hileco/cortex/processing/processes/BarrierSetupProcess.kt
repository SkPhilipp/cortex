package com.hileco.cortex.processing.processes

import com.hileco.cortex.ethereum.EthereumBarriers
import com.hileco.cortex.processing.database.ModelClient
import com.hileco.cortex.processing.database.NetworkModel
import com.hileco.cortex.processing.database.ProgramModel
import com.hileco.cortex.processing.geth.GethLoader
import java.math.BigDecimal

class BarrierSetupProcess : BaseProcess() {
    private val modelClient = ModelClient()
    private val gethLoader = GethLoader()
    private val ethereumBarriers = EthereumBarriers()

    @Suppress("NON_EXHAUSTIVE_WHEN")
    override fun run() {
        val networkModel = modelClient.networkProcessing() ?: return
        if (networkModel.name != LOCAL_NETWORK_NAME && networkModel.network != LOCAL_NETWORK) {
            return
        }
        barriersDeploy(networkModel)
        barriersAllocateBalance(networkModel)
        barriersTrace(networkModel)
    }

    private fun barriersDeploy(networkModel: NetworkModel) {
        val ethereumBarrierPrograms = ethereumBarriers.all()
        if (modelClient.programs(0, ethereumBarrierPrograms.size).toList().size >= ethereumBarrierPrograms.size) {
            return
        }
        ethereumBarriers.all().forEach { ethereumBarrier ->
            val result = gethLoader.executeGeth("setup-barrier-deploy.js", networkModel.networkAddress, ethereumBarrier.contractCode)
            println("Barrier Program Deploy ${ethereumBarrier.id}: $result")
        }
    }

    private fun barriersAllocateBalance(networkModel: NetworkModel) {
        val ethereumBarrierPrograms = ethereumBarriers.all()
        var programs = listOf<ProgramModel>()
        while (programs.size < ethereumBarrierPrograms.size) {
            programs = modelClient.programs(0, ethereumBarrierPrograms.size).toList()
            Thread.sleep(1000)
        }
        programs.forEach { programModel ->
            if (programModel.balance == BigDecimal.ZERO) {
                val result = gethLoader.executeGeth("setup-barrier-balance.js", networkModel.networkAddress, programModel.location.programAddress)
                println("Program Allocate ${programModel.location.programAddress}: $result")
            }
        }
    }

    private fun barriersTrace(networkModel: NetworkModel) {
        // debug.traceTransaction("0xa35e1b362299a921c9ee869acfef95427b406b04dc2876bd50d86902f5a16027")
    }

    companion object {
        const val LOCAL_NETWORK_NAME = "Ethereum"
        const val LOCAL_NETWORK = "local"
    }
}