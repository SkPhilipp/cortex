package com.hileco.cortex.documentation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

@SuppressWarnings("ReturnOfThis")
public class Document {
    private final String snippetPath;
    private final OutputStream outputStream;
    private final Consumer<IOException> exceptionHandler;

    Document(String snippetPath, OutputStream outputStream) {
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

    public Document source(String source) {
        return this.append("[source]\n", "```\n", source, "\n```\n\n");
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
}
