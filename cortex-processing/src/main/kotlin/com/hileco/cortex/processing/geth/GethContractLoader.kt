package com.hileco.cortex.processing.geth

import com.hileco.cortex.processing.database.BlockModel
import com.hileco.cortex.processing.database.NetworkModel

class GethContractLoader : GethLoader() {
    fun load(networkModel: NetworkModel, blockModel: BlockModel): List<GethContract> {
        val result = executeGeth("load-contracts.js", networkModel.networkAddress, blockModel.number)
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
