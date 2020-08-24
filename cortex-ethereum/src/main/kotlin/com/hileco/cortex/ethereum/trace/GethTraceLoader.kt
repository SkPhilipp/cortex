package com.hileco.cortex.ethereum.trace

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

class GethTraceLoader {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    fun loadTrace(resource: String): GethTrace {
        val traceRes = GethTraceLoader::class.java.getResource(resource)
        return objectMapper.readValue(traceRes)
    }
}
