package com.hileco.cortex.documentation.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.math.BigInteger

class ByteArraySerializer : JsonSerializer<ByteArray>() {
    override fun serialize(value: ByteArray, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeNumber(BigInteger(value))
    }
}
