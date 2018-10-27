package com.hileco.cortex.server.parsing;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigInteger;

public class ByteArraySerializer extends JsonSerializer<byte[]> {

    @Override
    public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(new BigInteger(value));
    }
}
