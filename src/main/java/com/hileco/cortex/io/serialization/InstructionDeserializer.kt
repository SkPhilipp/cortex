package com.hileco.cortex.io.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer
import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import com.hileco.cortex.instructions.Instruction
import java.io.IOException

class InstructionDeserializer : StdScalarDeserializer<Instruction>(Instruction::class.java) {
    private val stringDeserializer: StringDeserializer = StringDeserializer()
    private val instructionParser: InstructionParser = InstructionParser()

    @Throws(IOException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instruction? {
        val string = stringDeserializer.deserialize(p, ctxt) ?: return null
        try {
            return instructionParser.parse(string)
        } catch (e: IOException) {
            throw JsonMappingException(p, "Cloud not parse instruction", e)
        }
    }
}
