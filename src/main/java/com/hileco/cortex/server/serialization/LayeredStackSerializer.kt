package com.hileco.cortex.server.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer
import com.hileco.cortex.vm.layer.LayeredStack

class LayeredStackSerializer : StdScalarSerializer<LayeredStack<*>>(LayeredStack::class.java) {
    override fun serialize(layeredStack: LayeredStack<*>, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartArray()
        layeredStack.asSequence().forEach { gen.writeObject(it) }
        gen.writeEndArray()
    }
}
