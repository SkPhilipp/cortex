package com.hileco.cortex.processing.geth

import com.hileco.cortex.processing.database.NetworkModel
import java.math.BigDecimal

class GethContractLoader : GethLoader() {
    fun load(networkModel: NetworkModel, blockNumberStart: Int, blockNumberEnd: Int): List<GethContract> {
        val result = executeGeth("load-contracts.js", networkModel.networkAddress, blockNumberStart, blockNumberEnd)
        return result.elements()
                .asSequence()
                .map { element ->
                    GethContract(
                            bytecode = element.get("bytecode").asText(),
                            transactionHash = element.get("transactionHash").asText(),
                            address = element.get("address").asText(),
                            balance = element.get("balance").decimalValue()
                    )
                }
                .toList()
    }
}
