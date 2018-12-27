package com.hileco.cortex.documentation.source

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer
import com.fasterxml.jackson.databind.ser.std.StringSerializer
import com.hileco.cortex.constraints.expressions.Expression

import java.io.IOException

class ExpressionSerializer : StdScalarSerializer<Expression>(Expression::class.java) {

    private val stringSerializer: StringSerializer = StringSerializer()

    @Throws(IOException::class)
    override fun serialize(value: Expression?, gen: JsonGenerator, provider: SerializerProvider) {
        stringSerializer.serialize(value?.toString(), gen, provider)
    }
}
