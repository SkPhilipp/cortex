package com.hileco.cortex.server.parsing;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.hileco.cortex.instructions.Instruction;

import java.io.IOException;

public class InstructionSerializer extends StdScalarSerializer<Instruction> {

    private final StringSerializer stringSerializer;

    public InstructionSerializer() {
        super(Instruction.class);
        this.stringSerializer = new StringSerializer();
    }

    @Override
    public void serialize(Instruction value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        this.stringSerializer.serialize(value.toString(), gen, provider);
    }
}
