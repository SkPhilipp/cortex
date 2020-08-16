package com.hileco.cortex.processing.geth

import com.hileco.cortex.processing.database.NetworkModel
import java.math.BigDecimal

class GethContractLoader : GethLoader() {
    fun load(networkModel: NetworkModel, blockNumberStart: BigDecimal, blockNumberEnd: BigDecimal): List<GethContract> {
        val result = executeGeth("load-contracts.js", networkModel.networkAddress, blockNumberStart, blockNumberEnd)
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
