package com.hileco.cortex.io.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer
import com.hileco.cortex.vm.layer.LayeredStack

import java.io.IOException

class LayeredStackSerializer : StdScalarSerializer<LayeredStack<*>>(LayeredStack::class.java) {
    @Throws(IOException::class)
    override fun serialize(layeredStack: LayeredStack<*>, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartArray()
        for (element in layeredStack) {
            gen.writeObject(element)
        }
        gen.writeEndArray()
    }
}
