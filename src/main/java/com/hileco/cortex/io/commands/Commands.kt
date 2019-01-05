package com.hileco.cortex.io.commands

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer
import com.hileco.cortex.analysis.GraphBuilder
import com.hileco.cortex.analysis.processors.*
import com.hileco.cortex.instructions.Instruction
import com.hileco.cortex.io.serialization.ExpressionSerializer
import com.hileco.cortex.io.serialization.InstructionDeserializer
import com.hileco.cortex.io.serialization.InstructionSerializer
import com.hileco.cortex.io.serialization.LayeredStackSerializer

class Commands {
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
        val OPTIMIZED_GRAPH_BUILDER = GraphBuilder(listOf(
                ParameterProcessor(),
                FlowProcessor(),
                TrimEndProcessor(),
                DeadSwapProcessor(),
                JumpIllegalProcessor(),
                JumpThreadingProcessor(),
                JumpUnreachableProcessor(),
                KnownJumpIfProcessor(),
                KnownLoadProcessor(mapOf()),
                KnownProcessor(),
                FlowProcessor()
        ))
    }
}