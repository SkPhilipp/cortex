package com.hileco.cortex.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hileco.cortex.constraints.expressions.Expression;
import com.hileco.cortex.documentation.source.ByteArraySerializer;
import com.hileco.cortex.documentation.source.ExpressionSerializer;
import com.hileco.cortex.documentation.source.InstructionSerializer;
import com.hileco.cortex.documentation.source.LayeredStackSerializer;
import com.hileco.cortex.instructions.Instruction;
import com.hileco.cortex.vm.layer.LayeredStack;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Documentation {

    private static final Path DOCS_PATH;
    private static final Map<String, Document> OPEN_DOCUMENTS;
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        var userDir = System.getProperty("user.dir");
        DOCS_PATH = Paths.get(userDir, "build/generated-snippets");
        OPEN_DOCUMENTS = new HashMap<>();
        var module = new SimpleModule();
        module.addSerializer(LayeredStack.class, new LayeredStackSerializer());
        module.addSerializer(Instruction.class, new InstructionSerializer());
        module.addSerializer(Expression.class, new ExpressionSerializer());
        module.addSerializer(byte[].class, new ByteArraySerializer());
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        OBJECT_MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.registerModule(module);

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Document of(String snippetPath) {
        var separator = System.getProperty("file.separator");
        var path = DOCS_PATH.resolve(String.format("%s.adoc", snippetPath.replace("/", separator)));
        return OPEN_DOCUMENTS.computeIfAbsent(path.toString(), ignored -> {
            try {
                var file = path.toFile();
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                var outputStream = new FileOutputStream(file, !file.exists());
                return new Document(snippetPath, outputStream, OBJECT_MAPPER);
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Erred while interacting with file: %s", path), e);
            }
        });
    }
}
