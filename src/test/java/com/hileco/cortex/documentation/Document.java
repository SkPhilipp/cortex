package com.hileco.cortex.documentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hileco.cortex.instructions.Instruction;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("ReturnOfThis")
public class Document {
    private final ObjectMapper objectMapper;
    private final String snippetPath;
    private final OutputStream outputStream;
    private final Consumer<IOException> exceptionHandler;

    Document(String snippetPath, OutputStream outputStream, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.snippetPath = snippetPath;
        this.outputStream = outputStream;
        this.exceptionHandler = e -> {
            throw new IllegalStateException("Unexpected exception", e);
        };
    }

    private Document append(String... strings) {
        try {
            for (var string : strings) {
                this.outputStream.write(string.getBytes());
            }
        } catch (IOException e) {
            this.exceptionHandler.accept(e);
        }
        return this;
    }

    public Document source(List<? extends Instruction> instructions) {
        var source = instructions.stream().map(Objects::toString).collect(Collectors.joining("\n"));
        return this.append("[source]\n```\n", source, "\n```\n\n");
    }

    public Document source(Object source) {
        try {
            this.append("[source]\n```\n", this.objectMapper.writeValueAsString(source), "\n```\n\n");
        } catch (JsonProcessingException e) {
            this.exceptionHandler.accept(e);
        }
        return this;
    }

    public Document include(String otherSnippetPath) {
        return this.append("include::{snippets}/", otherSnippetPath, "[]\n\n");
    }

    public Document include(Document document) {
        return this.append("include::{snippets}/", document.snippetPath, "[]\n\n");
    }

    public Document headingDocument(String body) {
        return this.append("== ", body, "\n\n");
    }

    public Document headingSection(String body) {
        return this.append("=== ", body, "\n\n");
    }

    public Document headingParagraph(String body) {
        return this.append("==== ", body, "\n\n");
    }

    public Document paragraph(String body) {
        return this.append(body, "\n\n");
    }

    public Document image(byte[] bytes) {
        return this.append("++++\n<p style=\"text-align: center\">\n<img src=\"data:image/png;base64,",
                           Base64.getEncoder().encodeToString(bytes), "\"/>\n</p>\n++++\n\n");
    }
}
