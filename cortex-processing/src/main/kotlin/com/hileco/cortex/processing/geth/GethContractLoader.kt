package com.hileco.cortex.processing.geth

import com.hileco.cortex.processing.database.BlockModel
import com.hileco.cortex.processing.database.NetworkModel

class GethContractLoader : GethLoader() {
    private val loadContractsScript by lazy { unpackage("geth-scripts/load-contracts.js") }

    fun load(networkModel: NetworkModel, blockModel: BlockModel): List<GethContract> {
        val result = executeGethScript(loadContractsScript, mapOf(
                "networkAddress" to networkModel.networkAddress,
                "block" to blockModel.number.toString()
        ))
        return result.elements()
                .asSequence()
                .map { element ->
                    GethContract(
                            bytecode = element.get("bytecode").asText(),
                            transactionHash = element.get("transactionHash").asText(),
                            address = element.get("address").asText(),
                            currency = element.get("currency").decimalValue()
                    )
                }
                .toList()
    }
}
