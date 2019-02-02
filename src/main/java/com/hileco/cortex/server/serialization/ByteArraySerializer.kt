package com.hileco.cortex.server.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

import java.io.IOException
import java.math.BigInteger

class ByteArraySerializer : JsonSerializer<ByteArray>() {
    @Throws(IOException::class)
    override fun serialize(value: ByteArray, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeNumber(BigInteger(value))
    }
}
