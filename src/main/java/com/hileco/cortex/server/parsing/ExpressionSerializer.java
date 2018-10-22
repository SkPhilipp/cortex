package com.hileco.cortex.server.parsing;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.hileco.cortex.constraints.expressions.Expression;

import java.io.IOException;

public class ExpressionSerializer extends StdScalarSerializer<Expression> {

    private final StringSerializer stringSerializer;

    public ExpressionSerializer() {
        super(Expression.class);
        this.stringSerializer = new StringSerializer();
    }

    @Override
    public void serialize(Expression value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        this.stringSerializer.serialize(value == null ? null : value.toString(), gen, provider);
    }
}
