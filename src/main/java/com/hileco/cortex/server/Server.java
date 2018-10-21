package com.hileco.cortex.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.server.parsing.InstructionDeserializer;
import com.hileco.cortex.server.parsing.InstructionSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class Server {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        var module = new SimpleModule();
        module.addSerializer(Instruction.class, new InstructionSerializer());
        module.addDeserializer(Instruction.class, new InstructionDeserializer());
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        OBJECT_MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.registerModule(module);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
}
