package com.hileco.cortex.server.parsing;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.hileco.cortex.constraints.Solution;

import java.io.IOException;

public class SolutionSerializer extends StdScalarSerializer<Solution> {

    public SolutionSerializer() {
        super(Solution.class);
    }

    @Override
    public void serialize(Solution value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("possibleValues", value.getPossibleValues());
        gen.writeBooleanField("solvable", value.isSolvable());
        gen.writeEndObject();
    }
}
