package com.hileco.cortex.ethereum

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class EthereumBarriers(
        ethereumParser: EthereumParser = EthereumParser(),
        ethereumTranspiler: EthereumTranspiler = EthereumTranspiler(),
        objectMapper: ObjectMapper = jacksonObjectMapper()
) {

    private var barriers: List<EthereumBarrier>

    init {
        val jsonBarriers: JsonNode = objectMapper.readTree(javaClass.getResource("/barriers.json").readText())
        this.barriers = jsonBarriers.map { barrier ->
            val id = barrier.get("id").asText()
            val contractAddress = barrier.get("contractAddress").asText()
            val contractCode = barrier.get("contractCode").asText()
            val ethereumInstructions = ethereumParser.parse(contractCode.deserializeBytes())
            val cortexInstructions = ethereumTranspiler.transpile(ethereumInstructions)
            EthereumBarrier(id, contractAddress, contractCode, ethereumInstructions, cortexInstructions)
        }
    }

    fun all(): List<EthereumBarrier> {
        return barriers
    }
}