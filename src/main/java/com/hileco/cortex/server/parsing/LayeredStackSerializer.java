package com.hileco.cortex.server.parsing;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.hileco.cortex.vm.layer.LayeredStack;

import java.io.IOException;

public class LayeredStackSerializer extends StdScalarSerializer<LayeredStack> {

    public LayeredStackSerializer() {
        super(LayeredStack.class);
    }

    @Override
    public void serialize(LayeredStack layeredStack, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray();
        for (var element : layeredStack) {
            gen.writeObject(element);
        }
        gen.writeEndArray();
    }
}
