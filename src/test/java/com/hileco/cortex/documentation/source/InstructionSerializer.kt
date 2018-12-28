package com.hileco.cortex.documentation.source

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer
import com.fasterxml.jackson.databind.ser.std.StringSerializer
import com.hileco.cortex.instructions.Instruction

import java.io.IOException

class InstructionSerializer : StdScalarSerializer<Instruction>(Instruction::class.java) {
    private val stringSerializer: StringSerializer = StringSerializer()

    @Throws(IOException::class)
    override fun serialize(value: Instruction?, gen: JsonGenerator, provider: SerializerProvider) {
        stringSerializer.serialize(value?.toString(), gen, provider)
    }
}
