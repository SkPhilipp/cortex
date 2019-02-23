package com.hileco.cortex.server.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer
import com.fasterxml.jackson.databind.ser.std.StringSerializer
import com.hileco.cortex.constraints.expressions.Expression

class ExpressionSerializer : StdScalarSerializer<Expression>(Expression::class.java) {
    private val stringSerializer: StringSerializer = StringSerializer()

    override fun serialize(value: Expression?, gen: JsonGenerator, provider: SerializerProvider) {
        stringSerializer.serialize(value?.toString(), gen, provider)
    }
}
