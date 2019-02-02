package com.hileco.cortex.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer
import com.google.common.io.Resources
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.server.serialization.ExpressionSerializer
import com.hileco.cortex.server.serialization.InstructionDeserializer
import com.hileco.cortex.server.serialization.InstructionSerializer
import com.hileco.cortex.server.serialization.LayeredStackSerializer
import com.hubspot.jinjava.Jinjava

class Templates(private val jinjava: Jinjava = Jinjava()) {
    fun render(templateName: String, context: Map<String, Any> = mapOf()): String {
        val resource = Resources.getResource("templates/$templateName")
        val template = Resources.toString(resource, Charsets.UTF_8)
        return jinjava.render(template, context)
    }

    companion object {
        val OBJECT_MAPPER: ObjectMapper = ObjectMapper().let {
            val module = SimpleModule()
            module.addSerializer(ByteArraySerializer())
            module.addSerializer(ExpressionSerializer())
            module.addSerializer(InstructionSerializer())
            module.addSerializer(LayeredStackSerializer())
            module.addDeserializer(Instruction::class.java, InstructionDeserializer())
            it.registerModule(module)
            it.enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}
